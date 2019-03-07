package com.example.nfc_pay;


import java.io.IOException;
import java.io.UnsupportedEncodingException;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	private EditText mEtId;
	private Button writedata;
	String response;
	
	
	NfcAdapter adapter;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	boolean writeMode;
	Tag mytag;
	Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mEtId = (EditText)findViewById(R.id.et_proId);
		writedata = (Button) findViewById(R.id.write);
        
		writedata.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String carddata = mEtId.getText().toString().trim();
				
				try
				{
					Toast.makeText(getApplicationContext(), "carddata>>"+carddata, Toast.LENGTH_LONG).show();
					
					//String encrypteddata = AES_Encryption.encrypt(employeedata, "1111111111aaaaaa");
					String s1="Bangalore is a Garden City in India";
					
					byte out[]=XOR_Operation.xorWithData(s1.getBytes(), carddata.getBytes());
					String s3=new String(out);
					
					if(mytag==null)
					{
						Toast.makeText(ctx, ctx.getString(R.string.error_detected), Toast.LENGTH_LONG ).show();
					}
					else
					{
						// Call Write() Method to Write Details to NFC Tag //
						Toast.makeText(getApplicationContext(), "Encryption Success", Toast.LENGTH_LONG).show();
						
						write(s3,mytag);
						showCustomAlert();
						//Intent intent = new Intent(getApplicationContext(), MainActivity.class);
						//startActivity(intent);
						Toast.makeText(ctx, ctx.getString(R.string.ok_writing), Toast.LENGTH_LONG ).show();
						
					}

				}
				catch (Exception e)
				{
					//exception.setText("Exception:"+e.toString());
					System.out.println("********* Exception *********");
					//Toast.makeText(getApplicationContext(),"Exception"+e.toString(),Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
				
			}
		});
		
		
		adapter = NfcAdapter.getDefaultAdapter(this);
		
		// PendingIntent to run when tag gets scanned and Intent to start the current Activity
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		
		// ACTION_TAG_DISCOVERED will launch the onNewIntent method
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
		writeTagFilters = new IntentFilter[] {tagDetected };
        
    }
    
    
    public void showCustomAlert()
	{
	     
	    Context context = getApplicationContext();
	    // Create layout inflator object to inflate toast.xml file
	    LayoutInflater inflater = getLayoutInflater();
	      
	    // Call toast.xml file for toast layout 
	    View toastRoot = inflater.inflate(R.layout.success, null);
	      
	    Toast toast = new Toast(context);
	     
	    // Set layout to toast 
	    toast.setView(toastRoot);
	    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
	            0, 0);
	    toast.setDuration(Toast.LENGTH_LONG);
	    toast.show();
	     
	}

	@SuppressLint("NewApi")
	private void write(String text, Tag tag) throws IOException, FormatException
	{

		//Toast.makeText(getApplicationContext(), " Write() Method Called", Toast.LENGTH_LONG).show();
		
		NdefRecord[] records = {createRecord(text) };
		
		NdefMessage  message = new NdefMessage(records);
		
		//Toast.makeText(getApplicationContext(), "Start to Create Instance", Toast.LENGTH_LONG).show();
		
		// Get an instance of Ndef for the tag.
		Ndef ndef = Ndef.get(tag);
		
		//Toast.makeText(getApplicationContext(), "Enable Input and Output", Toast.LENGTH_LONG).show();
		
		// Enable I/O
		ndef.connect();
		Toast.makeText(getApplicationContext(), "Start to Write Data", Toast.LENGTH_LONG).show();
		
		// Write the message
		ndef.writeNdefMessage(message);
		// Close the connection
		ndef.close();
	}

	@SuppressLint("NewApi")
	private NdefRecord createRecord(String text) throws UnsupportedEncodingException
	{
		Toast.makeText(getApplicationContext(), "Start to createRecord", Toast.LENGTH_LONG).show();
		
		String lang       = "en";
		byte[] textBytes  = text.getBytes();
		byte[] langBytes  = lang.getBytes("US-ASCII");
		int    langLength = langBytes.length;
		int    textLength = textBytes.length;
		byte[] payload    = new byte[1 + langLength + textLength];

		// set status byte (see NDEF spec for actual bits)
		payload[0] = (byte) langLength;

		// copy langbytes and textbytes into payload
		System.arraycopy(langBytes, 0, payload, 1,              langLength);
		System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

		NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);

		return recordNFC;
	}

	@SuppressLint("NewApi")
	protected void onNewIntent(Intent intent)
	{
		// onNewIntent() to pull out the NFC Tag  
		if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()))
		{
			mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);    
			Toast.makeText(this, this.getString(R.string.ok_detection) + mytag.toString(), Toast.LENGTH_LONG ).show();
		}
	}

	@Override
	public void onPause()
	{
		super.onPause();
		WriteModeOff();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		WriteModeOn();
	}

	@SuppressLint("NewApi")
	private void WriteModeOn()
	{
		writeMode = true;
		adapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);


	}

	@SuppressLint("NewApi")
	private void WriteModeOff(){
		writeMode = false;
		adapter.disableForegroundDispatch(this);
	}


	


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		switch (item.getItemId())
        {
        case R.id.nfccardwrite:
           
            Toast.makeText(MainActivity.this, "User Selected  NFC writing Process", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            return true;
            
            
        case R.id.nfccardread:
            
            Toast.makeText(MainActivity.this, "User Selected  NFC Redaing Process", Toast.LENGTH_SHORT).show();
            Intent i1 = new Intent(getApplicationContext(), ReadFragment.class);
            startActivity(i1);
            return true;
            
 
 
        default:
            return super.onOptionsItemSelected(item);
        }
		
	}
}
