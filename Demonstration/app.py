from scapy.all import IP, TCP, send, UDP, Raw

SRC_IP =  "192.168.137.1"
DST_IP = "192.168.137.55"

ip = IP(src=SRC_IP, dst=DST_IP)
l4 = TCP(dport=5123, sport=12345)
payload = Raw(load="12345678901234567890")
send(ip/l4/payload)