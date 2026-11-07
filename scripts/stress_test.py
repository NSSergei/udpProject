import argparse
import json
import socket
import statistics
import threading
import time
import xml.etree.ElementTree as ET
from concurrent.futures import ThreadPoolExecutor


def percentile(values, percent):
    ordered = sorted(values)
    if not ordered:
        return None
    position = (len(ordered) - 1) * percent / 100
    lower = int(position)
    upper = min(lower + 1, len(ordered) - 1)
    fraction = position - lower
    return ordered[lower] * (1 - fraction) + ordered[upper] * fraction


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--host", default="127.0.0.1")
    parser.add_argument("--port", type=int, default=12345)
    parser.add_argument("--user-id", type=int, default=1)
    parser.add_argument("--requests", type=int, default=1000)
    parser.add_argument("--concurrency", type=int, default=32)
    parser.add_argument("--timeout", type=float, default=3)
    parser.add_argument("--output")
    args = parser.parse_args()
    if args.requests < 1 or args.concurrency < 1:
        parser.error("requests and concurrency must be positive")

    request = ET.Element("RequestData")
    ET.SubElement(request, "command").text = "info"
    ET.SubElement(request, "userId").text = str(args.user_id)
    payload = ET.tostring(request, encoding="utf-8")
    latencies = []
    errors = []
    lock = threading.Lock()
    start = time.perf_counter()

    def send_request(index):
        request_start = time.perf_counter()
        try:
            with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as client:
                client.settimeout(args.timeout)
                client.sendto(payload, (args.host, args.port))
                response, endpoint = client.recvfrom(65507)
                root = ET.fromstring(response)
                success = root.findtext("success", "").lower() == "true"
                if not success:
                    raise RuntimeError(root.findtext("message", "server returned failure"))
                elapsed_ms = (time.perf_counter() - request_start) * 1000
                with lock:
                    latencies.append(elapsed_ms)
        except Exception as error:
            with lock:
                errors.append({"request": index, "error": str(error)})

    with ThreadPoolExecutor(max_workers=args.concurrency) as executor:
        list(executor.map(send_request, range(args.requests)))

    duration = time.perf_counter() - start
    report = {
        "host": args.host,
        "port": args.port,
        "requests": args.requests,
        "concurrency": args.concurrency,
        "duration_seconds": round(duration, 3),
        "throughput_requests_per_second": round(args.requests / duration, 2),
        "successes": len(latencies),
        "errors": len(errors),
        "error_rate_percent": round(100 * len(errors) / args.requests, 2),
        "latency_ms": {
            "mean": round(statistics.fmean(latencies), 2) if latencies else None,
            "p50": round(percentile(latencies, 50), 2),
            "p95": round(percentile(latencies, 95), 2),
            "p99": round(percentile(latencies, 99), 2),
            "max": round(max(latencies), 2) if latencies else None,
        },
        "sample_errors": errors[:10],
    }
    rendered = json.dumps(report, indent=2)
    print(rendered)
    if args.output:
        with open(args.output, "w", encoding="utf-8") as output:
            output.write(rendered + "\n")
    if errors:
        raise SystemExit(1)


if __name__ == "__main__":
    main()
