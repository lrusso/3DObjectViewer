package ar.com.lrusso.dobjectviewer;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class myWebClient extends WebViewClient
	{
    public void onPageStarted(WebView view, String url, Bitmap favicon)
    	{
        super.onPageStarted(view, url, favicon);
    	}

    public boolean shouldOverrideUrlLoading(WebView view, String url)
    	{
        view.loadUrl(url);
        return true;
    	}

    public void onPageFinished(WebView view, String url)
    	{
        super.onPageFinished(view, url);
    	}
	}