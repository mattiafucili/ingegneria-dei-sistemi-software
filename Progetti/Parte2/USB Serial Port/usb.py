import serial
import time

serialData = serial.Serial(
    port='/dev/ttyACM0',
    baudrate=9600,
    parity=serial.PARITY_NONE,
    stopbits=serial.STOPBITS_ONE,
    bytesize=serial.EIGHTBITS,
    timeout=20000000
    )

while True:
    print (serialData.portstr)
    try:
        line = serialData.read(1024)
        if len(line) == 0: break
        print line
    except IOError:
	pass