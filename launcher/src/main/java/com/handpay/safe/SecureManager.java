package com.handpay.safe;

import com.handpay.launch.util.LogT;
import com.handpay.safe.crypto.AsymmetricBlockCipher;
import com.handpay.safe.crypto.encodings.PKCS1Encoding;
import com.handpay.safe.crypto.engines.RSAEngine;
import com.handpay.safe.crypto.params.RSAKeyParameters;

import java.math.BigInteger;
import java.security.Key;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public class SecureManager {

	private byte[] seed = null;
	private byte[] deskey = null;
	private long beginTime = -1;
	protected String securekeyindex = null;
	private static SecureManager sm;
	private static int DES_TYPE_ONE_DES = 0;
	private static int DES_TYPE_THREE_DES = 1;
	private int mCurrentDesType = DES_TYPE_THREE_DES;

	public void clearKey() {
		seed = null;
		deskey = null;
		beginTime = -1;
		securekeyindex = null;
	}

	public static SecureManager getInstance() {
		if (sm == null) {
			sm = new SecureManager();
		}
		return sm;
	}

	public void onDestroy() {
		sm = null;
	}

	private void CreateNewSeed() {
		int i;
		byte[] ct = new byte[8];
		byte[] desKey1 = { 0x68, 0x61, 0x6e, 0x64, 0x70, 0x61, 0x79, 0x21 };
		this.seed = new byte[16];
		// initseed
		for (i = 0; i < 16; i++) {
			double dr = Math.random();
			dr = dr * 0xff;
			int ri = (int) dr;
			this.seed[i] = (byte) ri;
		}
		/*
		 * randSeed[0]=0xe5; randSeed[1]=0x3b; randSeed[2]=0x8b;
		 * randSeed[3]=0x85; randSeed[4]=0x28; randSeed[5]=0x8b;
		 * randSeed[6]=0xa0; randSeed[7]=0x8e; randSeed[8]=0xff;
		 * randSeed[9]=0x58; randSeed[10]=0xbc; randSeed[11]=0xb5;
		 * randSeed[12]=0x23; randSeed[13]=0xc9; randSeed[14]=0x1e;
		 * randSeed[15]=0x95;
		 */

		for (i = 0; i < 8; i++) {
			ct[i] = (byte) (this.seed[i] ^ this.seed[i + 8]);
		}
		DesEncrypt des = new DesEncrypt();
		byte[] bs = des.Des(desKey1, ct, 1);
		if (mCurrentDesType == DES_TYPE_ONE_DES) {
			deskey = bs;
		} else {
			// 3des
			// 为了3des需要deskey长度增加一倍
			String strDesKey = byteTohex(bs) + byteTohex(bs) + byteTohex(bs);
			LogT.w("CreateNewSeed seed: " + byteTohex(seed));
			LogT.w("CreateNewSeed DesKey: " + strDesKey);
			deskey = hexTobyte(strDesKey);
		}
		this.securekeyindex = null;
		beginTime = -1;
	}

	private byte[] desfor24(byte[] data, byte[] key, int flag) {
		if (key != null && key.length != 24) {
			return null;
		}
		if (flag == 1) {
			CheckDesKey();
		}
		byte[] rdata = new byte[data.length];
		byte[] keyb = this.deskey;
		if (key != null) {
			keyb = key;
		}

		// for (int i = 0; i < data.length / 8; i++) {
		// DesEncrypt des = new DesEncrypt();
		// byte[] desdata = new byte[8];
		// for (int j = 0; j < 8; j++) {
		// desdata[j] = data[i * 8 + j];
		// }
		// byte[] rdes = des.Des(keyb, desdata, flag);
		// if (rdes != null && rdes.length == 8) {
		// for (int j = 0; j < 8; j++) {
		// rdata[i * 8 + j] = rdes[j];
		// }
		// } else {
		// return null;
		// }
		// }

		// yanglun start
		if (flag == 0) {
			rdata = decr(keyb, data);
		} else if (flag == 1) {
			LogT.w("save deskey:" + byteTohex(keyb));
			rdata = encr(keyb, data);
		}
		// yanglun end

		return rdata;
	}

	private byte[] desfor8(byte[] data, byte[] key, int flag) {
		if (data.length % 8 != 0 || (key != null && key.length != 8)) {
			return null;
		}
		if (flag == 1) {
			CheckDesKey();
		}
		byte[] rdata = new byte[data.length];
		byte[] keyb = this.deskey;
		if (key != null) {
			keyb = key;
		}
		for (int i = 0; i < data.length / 8; i++) {
			DesEncrypt des = new DesEncrypt();
			byte[] desdata = new byte[8];
			for (int j = 0; j < 8; j++) {
				desdata[j] = data[i * 8 + j];
			}
			byte[] rdes = des.Des(keyb, desdata, flag);
			if (rdes != null && rdes.length == 8) {
				for (int j = 0; j < 8; j++) {
					rdata[i * 8 + j] = rdes[j];
				}
			} else {
				return null;
			}
		}
		return rdata;
	}

	private int converCtoI(byte c) {
		if (c >= 48 && c < 58) {
			return c - 48;
		} else if (c >= 65 && c < 71) {
			return c - 65 + 10;
		} else if (c >= 97 && c < 103) {
			return c - 97 + 10;
		}
		return 0;
	}

	private byte[] hexTobyte(String hex) {
		if (hex != null) {
			byte[] source = hex.getBytes();
			byte[] resultdata = new byte[source.length / 2];
			for (int i = 0; i < source.length / 2; i++) {
				int tempi = converCtoI(source[2 * i]);
				resultdata[i] = (byte) ((tempi << 4) & 0xf0);
				resultdata[i] = (byte) (resultdata[i] + converCtoI(source[2 * i + 1]));
			}
			return resultdata;
		}
		return null;
	}

	private String byteTohex(byte[] data) {
		if (data != null) {
			char ac[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
			char[] red = new char[data.length * 2];
			for (int i = 0; i < data.length; i++) {
				red[2 * i] = ac[data[i] >> 4 & 0xf];
				red[2 * i + 1] = ac[data[i] & 0xf];
			}
			String string = new String(red);
			return string;
		}
		return null;
	}
	/**
	 * data 数据
	 * flag 1加密 0 解密
	 */
	public String des(String data, int flag, String key) {
		if (data != null) {
			try {
				byte[] keybyte = null;
				if (key != null) {
					keybyte = hexTobyte(key);
				}
				// treate data
				byte[] datas = null;
				if (flag == 1) {
					datas = data.getBytes("UTF-8");
					// datas = data.getBytes();
				} else {
					datas = this.hexTobyte(data);
				}

				// treate key

				byte[] tdd;
				if (mCurrentDesType == DES_TYPE_ONE_DES) {
					int num = (datas.length + 7) / 8;
					byte[] desbyte = new byte[num * 8];
					for (int i = 0; i < num * 8; i++) {
						if (i < datas.length) {
							desbyte[i] = datas[i];
						} else {
							desbyte[i] = 0;
						}
					}
					tdd = this.desfor8(desbyte, keybyte, flag);
				} else {
					// 3des
					tdd = this.desfor24(datas, keybyte, flag);
				}

				if (tdd != null) {
					if (flag == 1) {
						return this.byteTohex(tdd);
					} else {
						return new String(tdd).trim();
					}
				}
			} catch (Exception e) {
			}
		}
		return null;
	}

	public String md5(String data) {
		return MD5.toMD5(data);
	}

	public String mac(String data, String key) {
		String md5s = this.md5(data);
		if (md5s != null) {
			// treate key
			byte[] keybyte = null;
			if (key != null) {
				keybyte = hexTobyte(key);
			}
			byte[] md5byte = this.hexTobyte(md5s);
			if (md5byte != null && md5byte.length % 8 == 0) {
				byte[] macbyte = this.desfor8(md5byte, keybyte, 1);
				if (macbyte != null) {
					return this.byteTohex(macbyte);
				}
			}
		}
		return null;
	}

	public String CalcCRC(byte[] data) {
		String md5s = MD5.toMD5(data);
		if (md5s != null) {
			byte[] md5byte = this.hexTobyte(md5s);
			if (md5byte != null && md5byte.length % 8 == 0) {
				byte[] macbyte = this.desfor8(md5byte, null, 1);
				if (macbyte != null) {
					return this.byteTohex(macbyte);
				}
			}
		}
		return null;
	}

	public void CheckDesKey() {
		if (this.seed == null || this.deskey == null) {
			this.CreateNewSeed();
		}
		if (this.beginTime != -1) {
			long nowtime = System.currentTimeMillis();
			if ((nowtime - this.beginTime) > 1000 * 60 * 60 * 6) {
				this.CreateNewSeed();
			}
		}
	}

	public String Is_NeedCreateSeed() {
		if (beginTime == -1 && this.seed != null) {
			String hex = this.byteTohex(this.seed);
			hex = hex + "|";

			Calendar calendar = Calendar.getInstance();
			// 年
			hex = hex + calendar.get(Calendar.YEAR);
			// 月
			String tems = "00" + (calendar.get(Calendar.MONTH) + 1);
			hex = hex + tems.substring(tems.length() - 2);

			// 日
			tems = "00" + calendar.get(Calendar.DAY_OF_MONTH);
			hex = hex + tems.substring(tems.length() - 2);
			
			// 小时
			tems = "00" + calendar.get(Calendar.HOUR_OF_DAY);
			hex = hex + tems.substring(tems.length() - 2);

			// 分
			tems = "00" + calendar.get(Calendar.MINUTE);
			hex = hex + tems.substring(tems.length() - 2);
			// 秒
			tems = "00" + calendar.get(Calendar.SECOND);
			hex = hex + tems.substring(tems.length() - 2);

			return hex;
		}
		return null;
	}

	public void ReSetSecure(String keExchange) {
		LogT.w("ReSetSecure keExchange:" + keExchange);
		if (keExchange != null) {
			this.securekeyindex = keExchange;
			// 如果是第一次返回，记录该时间。其他的情况下不用更新该时间。
			if (-1 == beginTime) {
				this.beginTime = System.currentTimeMillis();
			}
		} else {
			this.CreateNewSeed();
		}
	}

	public static String sMod;
	public static String sPubExp;

	public String rsaEnCrypt(byte[] data) {
		try {
			BigInteger mod = null;
			BigInteger pubExp = null;
			if (sMod != null) {
				mod = new BigInteger(sMod, 16);
			}
			if (sPubExp != null) {
				pubExp = new BigInteger(sPubExp, 16);
			}
			RSAKeyParameters pubParameters = new RSAKeyParameters(false, mod, pubExp);
			AsymmetricBlockCipher eng = new RSAEngine();

			eng = new PKCS1Encoding(eng);

			eng.init(true, pubParameters);

			return byteTohex(eng.processBlock(data, 0, data.length));
		} catch (Exception e) {
			return "";
		}
	}

	// 用于加解密客户端本地的数据
	public String cupdes(String data, int flag) { // flag = 1 加密，flag =0, 解密

		String rDigits = null;

		String sID = "";

		Secure s = new Secure(rDigits, sID.getBytes());

		if (flag == 1) {
			String sData = "000000000" + data;
			sData = sData.substring(sData.length() - 8);
			return s.des8Bytes(sData.getBytes(), flag);
		} else {
			return s.des8Bytes(s.hexByte(data), flag);
		}
	}

	/**
	 * 加密
	 * 
	 * @author lfjiang 2016年3月22日
	 * @param key
	 * @param data
	 * @return
	 */
	public static byte[] encr(byte[] key, byte[] data) {
		try {
			Key k = toKey(key);
//			System.out.println(k.getEncoded().length * 8);
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITEM);
			cipher.init(Cipher.ENCRYPT_MODE, k);
			return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
			// logger.error("3DES encr exception", e);
			return null;
		}
	}

	/**
	 * 解密
	 * 
	 * @author lfjiang 2016年3月22日
	 * @param key
	 * @param data
	 * @return
	 */
	public static byte[] decr(byte[] key, byte[] data) {
		try {
			Key k = toKey(key);
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITEM);
			cipher.init(Cipher.DECRYPT_MODE, k);
			return cipher.doFinal(data);
		} catch (Exception e) {
			// logger.error("3DES decr exception", e);
			return null;
		}

	}

	private static final String KEY_ALGORITEM = "DESede";

	private static final String CIPHER_ALGORITEM = "DESede/ECB/PKCS5Padding";

	private static Key toKey(byte[] key) throws Exception {
		DESedeKeySpec dks = new DESedeKeySpec(key);
		SecretKeyFactory skf = SecretKeyFactory.getInstance(KEY_ALGORITEM);
		return skf.generateSecret(dks);
	}

}
