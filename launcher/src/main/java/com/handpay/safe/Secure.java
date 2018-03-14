package com.handpay.safe;

import java.io.ByteArrayOutputStream;
import java.util.zip.Adler32;

public class Secure {
	public static final String ERR_VERIFY_PIN_FAILED = "密码输入错误，请重新输入！提示：密码输入错误三次，系统将自动锁定智能存储卡。";

	private byte[] hpKey = new byte[8];
	private byte[] handpayID = new byte[8];
	private Adler32 crc = new Adler32();
	private DesEncrypt des = new DesEncrypt();

	private void init(String rDigits, byte[] handpayID) {
		if (rDigits == null)
			rDigits = "66778899";
		if (rDigits.length() < 8)
			rDigits = "66778899";
		if (handpayID.length == 8)
			this.handpayID = handpayID;

		try {
			byte[] hpKeyData = ERR_VERIFY_PIN_FAILED.substring(1, 8).getBytes(
					"UTF-8"); // 从其他类获取附加校验数据

			byte[] digits = rDigits.getBytes();

			for (int i = 0; i < 8; i++) {
				if ((digits[i] > 48) && (digits[i] < 58))
					hpKey[i] = hpKeyData[digits[i] - 48];
				else
					hpKey[i] = hpKeyData[0];
			}

		} catch (Exception e) {
		}
	}

	public Secure(String rDigits, byte[] handpayID) {
		init(rDigits, handpayID);
	}

	public Secure(String rDigits, String handpayID) {
		init(rDigits, hexByte(handpayID));
	}

	private long calc(byte[] data) {
		// 计算输入数据的CRC校验值
		try {
			byte[] exdata = des.Des(hpKey, handpayID, 1); // 加密附加校验数据
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			baos.write(exdata, 0, exdata.length);
			baos.write(data, 0, data.length);

			byte[] bytedata = baos.toByteArray();
			baos.close();

			crc.reset();
			crc.update(bytedata, 0, bytedata.length);

			return crc.getValue();
		} catch (Exception e) {
			return -1;
		}
	}

	public byte[] hexByte(String hex) {
		// 十六进制的ASCII码到二进制字节转换
		byte[] byte0 = new byte[hex.length() / 2];
		byte ac1[] = new byte[2];
		for (int i = 0; i < byte0.length; i++) {
			if (hex.charAt(i * 2) < 64)
				ac1[0] = (byte) (hex.charAt(i * 2) - 48);
			else
				ac1[0] = (byte) (hex.charAt(i * 2) - 55);

			if (hex.charAt(i * 2 + 1) < 64)
				ac1[1] = (byte) (hex.charAt(i * 2 + 1) - 48);
			else
				ac1[1] = (byte) (hex.charAt(i * 2 + 1) - 55);

			byte0[i] = (byte) (ac1[0] << 4 | ac1[1]);
			// Log.systemout(hex.substring(i*2,i*2+2)+" "+byte0[i]);
		}
		return byte0;
	}

	private String byteHEX(byte[] byte0) {
		// 字节到十六进制的ASCII码转换
		String s = "";
		char ac[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
				'B', 'C', 'D', 'E', 'F' };
		char ac1[] = new char[2];

		for (int i = 0; i < byte0.length; i++) {
			ac1[0] = ac[byte0[i] >> 4 & 0xf];
			ac1[1] = ac[byte0[i] & 0xf];
			s += new String(ac1);
		}
		return s;
	}

		// public String pack(String data) { // 把输入数据的前面加上CRC校验值
		//
		// try {
		// byte[] exdata = new byte[8];
		// long x = calc(data.getBytes("UTF-8"));
		//
		// for (int i = 0; i < 8; i++)
		// exdata[i] = (byte) (x >> (56 - i * 8));
		//
		// return byteHEX(exdata) + data;
		// } catch (Exception e) {
		// return null;
		// }
		// }

	public String calcCRC(byte[] data) {

		try {
			byte[] exdata = new byte[8];
			long x = calc(data);

			for (int i = 0; i < 8; i++)
				exdata[i] = (byte) (x >> (56 - i * 8));

			return byteHEX(exdata);
		} catch (Exception e) {
			return null;
		}
	}

	public boolean validate(byte[] data, String signature) {
		// 检验包含CRC校验值的输入数据是否通过校验

		try {
			// byte[] exdata = hexByte(data.substring(0, 16));
			//
			// long x = ((((long) exdata[0] & 0xff) << 56)
			// | (((long) exdata[1] & 0xff) << 48)
			// | (((long) exdata[2] & 0xff) << 40)
			// | (((long) exdata[3] & 0xff) << 32)
			// | (((long) exdata[4] & 0xff) << 24)
			// | (((long) exdata[5] & 0xff) << 16)
			// | (((long) exdata[6] & 0xff) << 8) | (((long) exdata[7] & 0xff)
			// << 0));

			String calced = this.calcCRC(data);
			return (calced != null && calced.compareTo(signature) == 0);
			// if (calc(data.substring(16).getBytes("UTF-8")) == x)
			// return true;
			// else
			// return false;
		} catch (Exception e) {
			return false;
		}
	}

	public String des16Bytes(String data, int flag) // flag=1 加密，flag=0 解密
	{// 将16Bytes的十六进制的ASCII码字符串加密或解密，用于Md5的密码的再加密或解密

		byte[] bytedata = hexByte(data);
		byte[] result = new byte[16];
		byte[] tmpdata = new byte[8];
		byte[] tmpresult = new byte[8];

		for (int i = 0; i < 8; i++)
			tmpdata[i] = bytedata[i];

		tmpresult = des.Des(hpKey, tmpdata, flag); // flag=1 加密，flag=0 解密

		for (int i = 0; i < 8; i++)
			result[i] = tmpresult[i];

		for (int i = 0; i < 8; i++)
			tmpdata[i] = bytedata[i + 8];

		tmpresult = des.Des(hpKey, tmpdata, flag); // flag=1 加密，flag=0 解密

		for (int i = 0; i < 8; i++)
			result[i + 8] = tmpresult[i];

		return byteHEX(result);

	}

	public String des8Bytes(byte[] data, int flag) // flag=1 加密，flag=0 解密
	{// 将8Bytes的十六进制的ASCII码字符串加密或解密
		byte[] result = new byte[8];

		result = des.Des(hpKey, data, flag); // flag=1 加密，flag=0 解密

		if (flag == 0)
			try {
				return new String(result);
			} catch (Exception e) {
				return byteHEX(result);
			}
		else
			return byteHEX(result);
	}
}
