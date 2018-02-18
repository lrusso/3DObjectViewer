package ar.com.lrusso.dobjectviewer;

import android.text.Html;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
	private Context context;
	private WebView webView;
	private ValueCallback<Uri> mUploadMessage;  
	private final static int FILECHOOSER_RESULTCODE=1;
	
	@Override protected void onCreate(Bundle savedInstanceState)
		{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		context = this;
		
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
        	});  
		}

	 @Override protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	 	{  
		if(requestCode==FILECHOOSER_RESULTCODE)  
			{  
			if (null == mUploadMessage) return;
			Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();  
			mUploadMessage.onReceiveValue(result);  
			mUploadMessage = null;  
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
		final String[] densityList = getResources().getStringArray(R.array.densityList);
		
		String currentDensityString = getDensity(); 
		int currentDensityIndex = 0;
		
		// KNOWING THE INDEX OF THE CURRENT DENSITY
		for(int i = 0; i < densityList.length - 1; i++)
			{
			if (densityList[i].equals(currentDensityString))
				{
				currentDensityIndex = i;
				}
			}
		
		new AlertDialog.Builder(this).setTitle(getString(R.string.textChangeDensity))
									 .setSingleChoiceItems(densityList, currentDensityIndex, null)
									 .setPositiveButton(R.string.textOK, new DialogInterface.OnClickListener()
									 	{
										public void onClick(DialogInterface dialog, int whichButton)
											{
											int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
											setDensity(densityList[selectedPosition]);
											loadConfigsAndWebView();
											}
									 	})
        .show();
		}

	private void clickInChangeSpeed()
		{
		final String[] speedList = getResources().getStringArray(R.array.speedList);
	
		String currentSpeedString = getSpeed(); 
		int currentSpeedIndex = 0;
	
		// KNOWING THE INDEX OF THE CURRENT PRINTING SPEED
		for(int i = 0; i < speedList.length - 1; i++)
			{
			if (speedList[i].equals(currentSpeedString))
				{
				currentSpeedIndex = i;
				}
			}
	
		new AlertDialog.Builder(this).setTitle(getString(R.string.textChangeSpeed))
									 .setSingleChoiceItems(speedList, currentSpeedIndex, null)
									 .setPositiveButton(R.string.textOK, new DialogInterface.OnClickListener()
									 	{
										public void onClick(DialogInterface dialog, int whichButton)
											{
											int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
											setSpeed(speedList[selectedPosition]);
											loadConfigsAndWebView();
											}
									 	})
		.show();
		}

	private void clickInChangeDiameter()
		{
		final String[] diameterList = getResources().getStringArray(R.array.diameterList);

		String currentDiameterString = getDiameter(); 
		int currentDiameterIndex = 0;

		// KNOWING THE INDEX OF THE CURRENT FILAMENT DIAMETER
		for(int i = 0; i < diameterList.length - 1; i++)
			{
			if (diameterList[i].equals(currentDiameterString))
				{
				currentDiameterIndex = i;
				}
			}

		new AlertDialog.Builder(this).setTitle(getString(R.string.textChangeDiameter))
									 .setSingleChoiceItems(diameterList, currentDiameterIndex, null)
									 .setPositiveButton(R.string.textOK, new DialogInterface.OnClickListener()
								 		{
										 	public void onClick(DialogInterface dialog, int whichButton)
										 	{
									 		int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
									 		setDiameter(diameterList[selectedPosition]);
									 		loadConfigsAndWebView();
										 	}
								 		})
		.show();
		}
	
	private void clickInChangeCost()
		{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		final EditText edittext = new EditText(this);
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
		        		setCost(value);
				 		loadConfigsAndWebView();
		        		}
		        		else
		        		{
		        		new AlertDialog.Builder(context).setTitle(getResources().getString(R.string.textError)).setMessage(getResources().getString(R.string.textInvalidNumber)).setPositiveButton(getResources().getString(R.string.textOK),new DialogInterface.OnClickListener()
		        			{
		        			public void onClick(DialogInterface dialog,int which)
		        				{
		        				}
		        			}).show();
		        		}
		        	}
		    	}
			});

		alert.show();
		}

	private void clickInAbout()
		{
		String years = "";
		String value = getResources().getString(R.string.textAboutMessage);
		int lastTwoDigits = Calendar.getInstance().get(Calendar.YEAR) % 100;
		if (lastTwoDigits<=5)
			{
			years = "2005";
			}
			else
			{
			years ="2005 - 20" + String.valueOf(lastTwoDigits).trim();
			}
	
		value = value.replace("ANOS", years);
	
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
        resultHTML = resultHTML.replace("var density = parseFloat('1.05');", "var density = parseFloat('" + getDensity() + "');");

		// SETTING THE COST VALUE
        resultHTML = resultHTML.replace("var filament_cost = parseFloat('200');", "var filament_cost = parseFloat('" + getCost() + "');");

        // SETTING THE FILAMENT DIAMETER VALUE
        resultHTML = resultHTML.replace("var filament_diameter = parseFloat('1.75');", "var filament_diameter = parseFloat('" + getDiameter() + "');");

		// SETTING THE PRINTING SPEED VALUE
        resultHTML = resultHTML.replace("var printing_speed = parseFloat('150');", "var printing_speed = parseFloat('" + getSpeed() + "');");

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
	}