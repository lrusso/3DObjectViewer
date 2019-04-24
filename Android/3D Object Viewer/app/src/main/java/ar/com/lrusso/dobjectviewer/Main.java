package ar.com.lrusso.dobjectviewer;

import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

public class Main extends Activity
	{
	private WebView webView;
	private static ValueCallback<Uri> mUploadMessage;  
	private static ValueCallback<Uri[]> mUploadMessage5;
	private final static int FILECHOOSER_RESULTCODE=1;
	public static DecimalFormat numberFormat = new DecimalFormat("0.00");

	@Override protected void onCreate(Bundle savedInstanceState)
		{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        
        loadConfigsAndWebView();
        
        webView.setWebViewClient(new myWebClient());
        webView.setWebChromeClient(new WebChromeClient()
        	{
        	// For Android 3.0+
        	public void openFileChooser(ValueCallback<Uri> uploadMsg)
        		{  
                mUploadMessage = uploadMsg;  
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
                i.addCategory(Intent.CATEGORY_OPENABLE);  
                i.setType("*/*");  
                startActivityForResult(Intent.createChooser(i,"File Chooser"), FILECHOOSER_RESULTCODE);  
        		}

            // For Android 3.0+
        	public void openFileChooser(ValueCallback uploadMsg, String acceptType)
        		{
        		mUploadMessage = uploadMsg;
        		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        		i.addCategory(Intent.CATEGORY_OPENABLE);
        		i.setType("*/*");
        		startActivityForResult(Intent.createChooser(i,"File Browser"),FILECHOOSER_RESULTCODE);
        		}

            //For Android 4.1
        	public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
        		{
        		mUploadMessage = uploadMsg;  
        		Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
        		i.addCategory(Intent.CATEGORY_OPENABLE);  
        		i.setType("*/*");  
        		startActivityForResult(Intent.createChooser(i,"File Chooser"),FILECHOOSER_RESULTCODE);
        		}

            // For Android 5.0
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, FileChooserParams fileChooserParams)
            	{
        		mUploadMessage5 = uploadMsg;
        		Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
        		i.addCategory(Intent.CATEGORY_OPENABLE);  
        		i.setType("*/*");  
        		startActivityForResult(Intent.createChooser(i,"File Chooser"),FILECHOOSER_RESULTCODE);
                return true;
            	}
        	});  

        if (Build.VERSION.SDK_INT>=23) //MARSHMALLOW
    		{
        	try
    			{
        		iniciarVerificacionMarshmallow();	
    			}
				catch(Exception e)
				{
				}
    		}
		}

	 @Override protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	 	{  
		if (resultCode == RESULT_OK)
			{
			if(requestCode==FILECHOOSER_RESULTCODE)  
				{  
				if (Build.VERSION.SDK_INT>=21) //LOLLIPOP
	        		{
					try
	        			{
						Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
						mUploadMessage5.onReceiveValue(new Uri[]{result});  
						mUploadMessage5 = null;  
	        			}
	        			catch(Exception e)
	        			{
	        			}
	        		}
	        	}
	        	else
	        	{
	        	try
	        		{
		       		if (null == mUploadMessage) return;
		       		Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();  
		       		mUploadMessage.onReceiveValue(result);  
		       		mUploadMessage = null;  
	        		}
	        		catch(Exception e)
	        		{
	        		}
	        	}
	        }
			else
			{
			try
				{
				if (mUploadMessage5 != null)
					{
					mUploadMessage5.onReceiveValue(null);
					mUploadMessage5 = null;
					}

				if (mUploadMessage != null)
					{
					mUploadMessage.onReceiveValue(null);
					mUploadMessage = null;
					}
				}
				catch(Exception e)
				{
				}
			}
	 	}
	
	@Override public boolean onCreateOptionsMenu(Menu menu)
		{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
		}

	@Override public boolean onOptionsItemSelected(MenuItem item)
		{
	    switch (item.getItemId())
    		{
    		case R.id.action_settings:
    		View menuItemView = findViewById(R.id.action_settings);
    		PopupMenu popupMenu = new PopupMenu(this, menuItemView); 
    		popupMenu.inflate(R.menu.popup_menu);
    		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
				{  
    			public boolean onMenuItemClick(MenuItem item)
    				{
	    			if (item.getTitle().toString().contains(getResources().getString(R.string.textPrivacy)))
						{
	    				clickInPrivacy();
						}
	    			else if (item.getTitle().toString().contains(getResources().getString(R.string.textChangeDensity)))
						{
	    				clickInChangeDensity();
						}
	    			else if (item.getTitle().toString().contains(getResources().getString(R.string.textChangeSpeed)))
						{
	    				clickInChangeSpeed();
						}
	    			else if (item.getTitle().toString().contains(getResources().getString(R.string.textChangeDiameter)))
						{
	    				clickInChangeDiameter();
						}
	    			else if (item.getTitle().toString().contains(getResources().getString(R.string.textChangeCost)))
						{
	    				clickInChangeCost();
						}
	    			else if (item.getTitle().toString().contains(getResources().getString(R.string.textAbout)))
    					{
    					clickInAbout();
    					}
    				return true;  
    				}
				});
    		popupMenu.show();
    		return true;
    		
    		default:
    		return super.onOptionsItemSelected(item);
    		}
		}
	
	private void clickInPrivacy()
		{
		LayoutInflater inflater = LayoutInflater.from(this);
		View view=inflater.inflate(R.layout.privacy, null);

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);  
		alertDialog.setTitle(getResources().getString(R.string.textPrivacy));  
		alertDialog.setView(view);
		alertDialog.setPositiveButton(getResources().getString(R.string.textOK), new DialogInterface.OnClickListener()
			{
			public void onClick(DialogInterface dialog, int whichButton)
				{
				}
			});
		alertDialog.show();
		}
	
	private void clickInChangeDensity()
		{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		final EditText edittext = new EditText(this);
		edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		edittext.setText(String.valueOf(numberFormat.format(Double.valueOf(getDensity()))));
		edittext.setFilters(new InputFilter[] {new InputFilter.LengthFilter(5),new DecimalDigitsInputFilter(2)});
		
		alert.setTitle(getResources().getString(R.string.textChangeDensity));

		alert.setView(edittext);

		alert.setPositiveButton(getResources().getString(R.string.textOK), new DialogInterface.OnClickListener()
			{
		    public void onClick(DialogInterface dialog, int whichButton)
		    	{
		        String value = edittext.getText().toString();
		        value = value.trim();
		        if (value.length()>0)
		        	{
		        	if (isNumeric(value)==true)
		        		{
		        		if (Double.valueOf(value)>0)
		        			{
		        			value = value.replaceFirst("^0+(?!$)", "");
			        		setDensity(String.valueOf(Double.valueOf(value)));
					 		loadConfigsAndWebView();
		        			}
		        		}
		        	}
		    	}
			});

		alert.show();
		}

	private void clickInChangeSpeed()
		{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		final EditText edittext = new EditText(this);
		edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
		edittext.setText(getSpeed());
		
		alert.setTitle(getResources().getString(R.string.textChangeSpeed));

		alert.setView(edittext);

		alert.setPositiveButton(getResources().getString(R.string.textOK), new DialogInterface.OnClickListener()
			{
		    public void onClick(DialogInterface dialog, int whichButton)
		    	{
		        String value = edittext.getText().toString();
		        value = value.trim();
		        if (value.length()>0)
		        	{
		        	if (isNumeric(value)==true)
		        		{
		        		if (Double.valueOf(value)>0)
		        			{
		        			value = value.replaceFirst("^0+(?!$)", "");
			        		setSpeed(value);
					 		loadConfigsAndWebView();
		        			}
		        		}
		        	}
		    	}
			});

		alert.show();
		}

	private void clickInChangeDiameter()
		{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		final EditText edittext = new EditText(this);
		edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		edittext.setText(String.valueOf(numberFormat.format(Double.valueOf(getDiameter()))));
		edittext.setFilters(new InputFilter[] {new InputFilter.LengthFilter(5),new DecimalDigitsInputFilter(2)});
		
		alert.setTitle(getResources().getString(R.string.textChangeDiameter));

		alert.setView(edittext);

		alert.setPositiveButton(getResources().getString(R.string.textOK), new DialogInterface.OnClickListener()
			{
		    public void onClick(DialogInterface dialog, int whichButton)
		    	{
		        String value = edittext.getText().toString();
		        value = value.trim();
		        if (value.length()>0)
		        	{
		        	if (isNumeric(value)==true)
		        		{
		        		if (Double.valueOf(value)>0)
		        			{
		        			value = value.replaceFirst("^0+(?!$)", "");
			        		setDiameter(String.valueOf(Double.valueOf(value)));
					 		loadConfigsAndWebView();
		        			}
		        		}
		        	}
		    	}
			});

		alert.show();		
		}
	
	private void clickInChangeCost()
		{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		final EditText edittext = new EditText(this);
		edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
		edittext.setText(getCost());
		
		alert.setTitle(getResources().getString(R.string.textChangeCost));

		alert.setView(edittext);

		alert.setPositiveButton(getResources().getString(R.string.textOK), new DialogInterface.OnClickListener()
			{
		    public void onClick(DialogInterface dialog, int whichButton)
		    	{
		        String value = edittext.getText().toString();
		        value = value.trim();
		        if (value.length()>0)
		        	{
		        	if (isNumeric(value)==true)
		        		{
		        		if (Double.valueOf(value)>0)
		        			{
		        			value = value.replaceFirst("^0+(?!$)", "");
			        		setCost(value);
					 		loadConfigsAndWebView();
		        			}
		        		}
		        	}
		    	}
			});

		alert.show();
		}

	private void clickInAbout()
		{
		String value = getResources().getString(R.string.textAboutMessage);
		value = value.replace("APPNAME",getResources().getString(R.string.app_name));

		TextView msg = new TextView(this);
		msg.setText(Html.fromHtml(value));
		msg.setPadding(10, 20, 10, 25);
		msg.setGravity(Gravity.CENTER);
		float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
		float size = new EditText(this).getTextSize() / scaledDensity;
		msg.setTextSize(size);

		new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.textAbout)).setView(msg).setIcon(R.drawable.ic_launcher).setPositiveButton(getResources().getString(R.string.textOK),new DialogInterface.OnClickListener()
			{
			public void onClick(DialogInterface dialog,int which)
				{
				}
			}).show();
		}
	
	private String getDensity()
		{
		String result = "";
		DataInputStream in = null;
		try
    		{
			in = new DataInputStream(openFileInput("density.cfg"));
			for (;;)
        		{
				result = result + in.readUTF();
        		}
    		}
    		catch (Exception e)
    		{
    		}
		try
    		{
			in.close();
    		}
    		catch(Exception e)
    		{
    		}
		if (result=="")
			{
			result = "1.05";
			}
		return result;
		}
	
	private void setDensity(String density)
		{
		try
			{
			DataOutputStream out = new DataOutputStream(openFileOutput("density.cfg", Context.MODE_PRIVATE));
			out.writeUTF(density);
			out.close();
			}
	    	catch(Exception e)
	    	{
	    	}
		}

	private String getDiameter()
		{
		String result = "";
		DataInputStream in = null;
		try
			{
			in = new DataInputStream(openFileInput("diameter.cfg"));
			for (;;)
    			{
				result = result + in.readUTF();
    			}
			}
			catch (Exception e)
			{
			}
		try
			{
			in.close();
			}
			catch(Exception e)
			{
			}
		if (result=="")
			{
			result = "1.75";
			}
		return result;
		}

	private void setDiameter(String diameter)
		{
		try
			{
			DataOutputStream out = new DataOutputStream(openFileOutput("diameter.cfg", Context.MODE_PRIVATE));
			out.writeUTF(diameter);
			out.close();
			}
	    	catch(Exception e)
	    	{
	    	}
		}	

	private String getSpeed()
		{
		String result = "";
		DataInputStream in = null;
		try
			{
			in = new DataInputStream(openFileInput("speed.cfg"));
			for (;;)
				{
				result = result + in.readUTF();
				}
			}
			catch (Exception e)
			{
			}
		try
			{
			in.close();
			}
			catch(Exception e)
			{
			}
		if (result=="")
			{
			result = "150";
			}
		return result;
		}

	private void setSpeed(String speed)
		{
		try
			{
			DataOutputStream out = new DataOutputStream(openFileOutput("speed.cfg", Context.MODE_PRIVATE));
			out.writeUTF(speed);
			out.close();
			}
    		catch(Exception e)
    		{
    		}
		}

	private String getCost()
		{
		String result = "";
		DataInputStream in = null;
		try
			{
			in = new DataInputStream(openFileInput("cost.cfg"));
			for (;;)
				{
				result = result + in.readUTF();
				}
			}
			catch (Exception e)
			{
			}
		try
			{
			in.close();
			}
			catch(Exception e)
			{
			}
		if (result=="")
			{
			result = "200";
			}
		return result;
		}

	private void setCost(String cost)
		{
		try
			{
			DataOutputStream out = new DataOutputStream(openFileOutput("cost.cfg", Context.MODE_PRIVATE));
			out.writeUTF(cost);
			out.close();
			}
			catch(Exception e)
			{
			}
		}

	private boolean isNumeric(String str)  
		{
		try
			{
			double d = Double.parseDouble(str);  
			}
			catch(NumberFormatException nfe)  
			{  
			return false;  
			} 
		return true;  
		}

	private void loadConfigsAndWebView()
		{
        // LOADING THE HTML DOCUMENT
		String resultHTML = loadAssetTextAsString("3DObjectViewer.htm");
            
		// SETTING THE DENSITY VALUE
        resultHTML = resultHTML.replace("var density = parseFloat(\"1.05\");", "var density = parseFloat(\"" + getDensity() + "\");");

		// SETTING THE COST VALUE
        resultHTML = resultHTML.replace("var filament_cost = parseFloat(\"200\");", "var filament_cost = parseFloat(\"" + getCost() + "\");");

        // SETTING THE FILAMENT DIAMETER VALUE
        resultHTML = resultHTML.replace("var filament_diameter = parseFloat(\"1.75\");", "var filament_diameter = parseFloat(\"" + getDiameter() + "\");");

		// SETTING THE PRINTING SPEED VALUE
        resultHTML = resultHTML.replace("var printing_speed = parseFloat(\"150\");", "var printing_speed = parseFloat(\"" + getSpeed() + "\");");

        // LOADING THE WEBVIEW
        webView.loadDataWithBaseURL(null, resultHTML, null, "utf-8", null);
		}
	
	private String loadAssetTextAsString(String name)
		{
        BufferedReader in = null;
        try
        	{
            StringBuilder buf = new StringBuilder();
            InputStream is = getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str=in.readLine())!=null)
            	{
                if (isFirst)
                	{
                    isFirst = false;
                	}
                	else
                    {
                	buf.append("\n");
                    }
                buf.append(str);
            	}
            return buf.toString();
        	}
        	catch (IOException e)
        	{
        	}
        	finally
        	{
            if (in!=null)
            	{
                try
                	{
                    in.close();
                	}
                	catch (IOException e)
                	{
                	}
            	}
        	}
        return null;
		}
	
	private class DecimalDigitsInputFilter implements InputFilter
		{

		  private final int decimalDigits;

		  /**
		   * Constructor.
		   * 
		   * @param decimalDigits maximum decimal digits
		   */
		  public DecimalDigitsInputFilter(int decimalDigits) {
		    this.decimalDigits = decimalDigits;
		  }

		  @Override
		  public CharSequence filter(CharSequence source,
		      int start,
		      int end,
		      Spanned dest,
		      int dstart,
		      int dend) {


		    int dotPos = -1;
		    int len = dest.length();
		    for (int i = 0; i < len; i++) {
		      char c = dest.charAt(i);
		      if (c == '.' || c == ',') {
		        dotPos = i;
		        break;
		      }
		    }
		    if (dotPos >= 0) {

		      // protects against many dots
		      if (source.equals(".") || source.equals(","))
		      {
		          return "";
		      }
		      // if the text is entered before the dot
		      if (dend <= dotPos) {
		        return null;
		      }
		      if (len - dotPos > decimalDigits) {
		        return "";
		      }
		    }

		    return null;
		  }
		}
	
	@TargetApi(Build.VERSION_CODES.M)
	public void iniciarVerificacionMarshmallow()
		{
		if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
			{
			String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
											Manifest.permission.WRITE_EXTERNAL_STORAGE};
			requestPermissions(PERMISSIONS_STORAGE, 123);
			}
		}
		
	@TargetApi(Build.VERSION_CODES.M)
	@Override public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
		{
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==123)
        	{
        	if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        		{
        		}
        	}
		}
	}