package com.vjcrackers.textencryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("WorldReadableFiles")
public class MainActivity extends Activity {
	
	private Cipher ecipher;
    private Cipher dcipher;
    private final static byte[] salt = {
        (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
        (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03 };
    private int iterationCount = 19;
	private EditText editText1,editText2,editText3,editText4;
	private Button rstButton,clsButton,savButton;
	private TextView numberView;
	private ImageButton randButton;
	static final int READ_BLOCK_SIZE = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		editText1 = (EditText) findViewById(R.id.editText1);
		editText2 = (EditText) findViewById(R.id.editText2);
		editText3 = (EditText) findViewById(R.id.editText3);
		editText4 = (EditText) findViewById(R.id.editText4);		
		numberView = (TextView) findViewById(R.id.dspNumber);	
		rstButton = (Button) findViewById(R.id.button3);
		clsButton = (Button) findViewById(R.id.button4);
		savButton = (Button) findViewById(R.id.button5);
		randButton = (ImageButton) findViewById(R.id.randButton); 
		
		/*Number Generation */
		randButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v)
			{
				Random randomGenerator = new Random();  
		        int x = getNumber();
		        randomGenerator.setSeed(x);    
		        long randomNumber = randomGenerator.nextInt(90000000-10000000);
		        String ranNumber = String.valueOf(randomNumber);
		        numberView.setText(ranNumber);
			}
			private int getNumber() 
			{
		         return (int)((Math.random()*26)+(100-8));
			}
		});
		
		/* Reset Button */
		rstButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) 
			{
				editText1.setText(null);
				editText2.setText(null);
				editText3.setText(null);
				editText4.setText(null);
				numberView.setText(null);
				editText1.requestFocus();
			}
		});	
		
		/* Close Button */
		clsButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) 
			{
				System.exit(100);
			}
		});	
		
		
		/* Saving Part */
		savButton.setOnClickListener(new OnClickListener()
		{
			public void onClick (View v) 
			{
				String txt = editText3.getText().toString();
				String filName = editText1.getText().toString();
				String name = null;
				if(filName.equals(""))
				{
					Toast.makeText(getBaseContext(), "File name is Required", Toast.LENGTH_LONG).show();
					editText1.requestFocus();
				}
				else
				{
					try {
						name = filName + ".txt";
						File sdcard = Environment.getExternalStorageDirectory();
						File directory = new File(sdcard.getAbsolutePath()+ "/Confidroid");
						directory.mkdirs();
						File file = new File(directory,name);
						FileOutputStream fOut = new FileOutputStream(file);
						/*FileOutputStream fOut=openFileOutput(name, MODE_WORLD_READABLE);*/
						OutputStreamWriter osw = new OutputStreamWriter(fOut);						
						osw.write(txt);
						osw.flush();
						osw.close();
						Toast.makeText(getBaseContext(), name +" Stored Successfully", Toast.LENGTH_LONG).show();
						editText1.setText(null);
						editText2.setText(null);
						editText3.setText(null);
						editText4.setText(null);
						numberView.setText(null);
					}
					catch(IOException ioe)
					{
						ioe.printStackTrace();
						Toast.makeText(getBaseContext(),"Unable to perform any Operation!!!", Toast.LENGTH_LONG).show();
					}
				}
			}
		});
	}			
		/* Encrypt Part */
			public void onClickEncrypt(View v) 
			{
				String passwd = editText2.getText().toString();
				String sec_key = editText4.getText().toString();
				if(passwd.length()>0 && sec_key.length()>0)
				{
					String passkey = passwd + sec_key;
		        String plaintext = editText3.getText().toString();
		        try {
					String enc = encrypt(passkey, plaintext);
					editText3.setText(enc);
					Toast.makeText(getBaseContext(), "Text Encrypted Successfully", Toast.LENGTH_LONG).show();
		        }
				catch(IOException ioe)
				{
					ioe.printStackTrace();
				} catch (InvalidKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidAlgorithmParameterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
				else
				{
					Toast.makeText(getBaseContext(), "Password & Secure Key is Required", Toast.LENGTH_LONG).show();
					editText2.requestFocus();
				}
			}
			private String encrypt(String secretKey, String plainText) throws NoSuchAlgorithmException, 
            	InvalidKeySpecException, 
            	NoSuchPaddingException, 
            	InvalidKeyException,
            	InvalidAlgorithmParameterException, 
            	UnsupportedEncodingException, 
            	IllegalBlockSizeException, 
            	BadPaddingException {
				//Key generation for enc and desc
		        KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount);
		        SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);        
		         // Prepare the parameter to the ciphers
		        AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
		        //Enc process
		        ecipher = Cipher.getInstance(key.getAlgorithm());
		        ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);      
		        String charSet="UTF-8";       
		        byte[] in = plainText.getBytes(charSet);
		        byte[] out = ecipher.doFinal(in);
		        String encStr=new BASE64Encoder().encode(out);
		        return encStr;
			}	
			
			/* Decrypt Part */	
			public void onClickLoad(View v) 
			{
				String filName = editText1.getText().toString();
				String passwd = editText2.getText().toString();
				String sec_key = editText4.getText().toString();
				String key = passwd + sec_key;
				if(filName.length()>0)
				{
					if(passwd.length()>0 && sec_key.length()>0)
					{
						try
						{					
							String name = filName + ".txt";		
							File sdcard = Environment.getExternalStorageDirectory();
							File directory = new File(sdcard.getAbsolutePath()+"/Confidroid");
							File file = new File(directory,name);
							FileInputStream fIn = new FileInputStream(file);
							@SuppressWarnings("resource")
							InputStreamReader isr= new InputStreamReader(fIn);
					
							char[] inputBuffer = new char[READ_BLOCK_SIZE];
							String enc= "";
							int charRead;
							while((charRead=isr.read(inputBuffer))>0)
							{
								String readString=String.copyValueOf(inputBuffer,0,charRead);
								enc+=readString;
								inputBuffer=new char[READ_BLOCK_SIZE];
							}
							String plainAfter= decrypt(key, enc);
							editText3.setText(plainAfter);
							Toast.makeText(getBaseContext(), name+" Decrypted Successfully", Toast.LENGTH_LONG).show();
						}
						catch(IOException ioe)
						{
							ioe.printStackTrace();
						} catch (InvalidKeyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvalidKeySpecException e) {
						// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchPaddingException e) {
						// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvalidAlgorithmParameterException e) {
						// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalBlockSizeException e) {
						// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (BadPaddingException e) {
						// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else
					{
						Toast.makeText(getBaseContext(), "Password & Secure Key is Required", Toast.LENGTH_LONG).show();
						editText2.requestFocus();
					}
				}
				else
				{
					Toast.makeText(getBaseContext(), "File Name is Required", Toast.LENGTH_LONG).show();
					editText1.requestFocus();
				}
			}
			private String decrypt(String secretKey, String encryptedText) throws NoSuchAlgorithmException, 
            InvalidKeySpecException, 
            NoSuchPaddingException, 
            InvalidKeyException,
            InvalidAlgorithmParameterException, 
            UnsupportedEncodingException, 
            IllegalBlockSizeException, 
            BadPaddingException, 
            IOException	{
				//Key generation for enc and desc
				KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount);
				SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);        
				// Prepare the parameter to the ciphers
				AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
				//Decryption process; same key will be used for decr
				dcipher=Cipher.getInstance(key.getAlgorithm());
				dcipher.init(Cipher.DECRYPT_MODE, key,paramSpec);
				byte[] enc = new BASE64Decoder().decodeBuffer(encryptedText);
				byte[] utf8 = dcipher.doFinal(enc);
				String charSet="UTF-8";     
				String plainStr = new String(utf8, charSet);
				return plainStr;
			}			
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
