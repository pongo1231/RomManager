package com.androidfany.rommanager.Helper;

import android.content.*;
import android.graphics.*;
import android.net.*;
import android.text.*;
import android.widget.*;
import com.androidfany.rommanager.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;

public class MethodHolder
{

	private static Context context;

	public MethodHolder(final Context exContext)
	{
		context = exContext;
	}

	private static final String FILE1 = "roms.json";
	private static final String FILE2 = "desc.json";
	private static final String FILE3 = "update.json";
	private static final String URL1 = "http://acedb.grn.cc/ace-i/roms.json";
	private static final String URL2 = "http://acedb.grn.cc/ace-i/desc.json";
	private static final String URL3 = "http://acedb.grn.cc/ace-i/update.json";

	public void updateJson()
	{
		int count;
		try
		{
			final URL url1 = new URL(URL1);
			final URL url2 = new URL(URL2);
			final URL url3 = new URL(URL3);

			final URLConnection connection1 = url1.openConnection();
			final URLConnection connection2 = url2.openConnection();
			final URLConnection connection3 = url3.openConnection();
			connection1.connect();
			connection2.connect();
			connection3.connect();

			// download the file
			final InputStream input1 = new BufferedInputStream(url1.openStream(), 8192);
			final InputStream input2 = new BufferedInputStream(url2.openStream(), 8192);
			final InputStream input3 = new BufferedInputStream(url3.openStream(), 8192);

			// Output stream
			final OutputStream output1 = new FileOutputStream(context.getFilesDir().getPath() + "/" + FILE1);
			final OutputStream output2 = new FileOutputStream(context.getFilesDir().getPath() + "/" + FILE2);
			final OutputStream output3 = new FileOutputStream(context.getFilesDir().getPath() + "/" + FILE3);


			final byte data1[] = new byte[1024];
			final byte data2[] = new byte[1024];
			final byte data3[] = new byte[1024];

			while ((count = input1.read(data1)) != -1)
			{
				output1.write(data1, 0, count);
			}

			while ((count = input2.read(data2)) != -1)
			{
				output2.write(data2, 0, count);
			}

			while ((count = input3.read(data3)) != -1)
			{
				output3.write(data3, 0, count);
			}

			// flushing output
			output1.flush();
			output2.flush();
			output3.flush();

			// closing streams
			output1.close();
			output2.close();
			output3.close();
			input1.close();
			input2.close();
			input3.close();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getRomsJson()
	{
		String json = null;

		try
		{
			final FileInputStream is = new FileInputStream(context.getFilesDir().getPath() + "/" + FILE1);
			final int size = is.available();
			final byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			json = new String(buffer, "UTF-8");
		}
		catch (final IOException ex)
		{
			ex.printStackTrace();
			return null;
		}
		return json;
	}

	public String getDescJson()
	{
		String json = null;

		try
		{
			final FileInputStream is = new FileInputStream(context.getFilesDir().getPath() + "/" + FILE2);
			final int size = is.available();
			final byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			json = new String(buffer, "UTF-8");
		}
		catch (final IOException ex)
		{
			ex.printStackTrace();
			return null;
		}
		return json;
	}

	public boolean hasUserInternet()
	{
		final ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected())
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public Spanned bbcode(final String text)
	{
		String html = text;

		final Map<String, String> bbMap = new HashMap<String, String>();

		bbMap.put("(\r\n|\r|\n|\n\r)", "<br/>");
		bbMap.put("\\[b\\](.+?)\\[/b\\]", "<strong>$1</strong>");
		bbMap.put("\\[i\\](.+?)\\[/i\\]", "<span style='font-style:italic;'>$1</span>");
		bbMap.put("\\[u\\](.+?)\\[/u\\]", "<span style='text-decoration:underline;'>$1</span>");
		bbMap.put("\\[h1\\](.+?)\\[/h1\\]", "<h1>$1</h1>");
		bbMap.put("\\[h2\\](.+?)\\[/h2\\]", "<h2>$1</h2>");
		bbMap.put("\\[h3\\](.+?)\\[/h3\\]", "<h3>$1</h3>");
		bbMap.put("\\[h4\\](.+?)\\[/h4\\]", "<h4>$1</h4>");
		bbMap.put("\\[h5\\](.+?)\\[/h5\\]", "<h5>$1</h5>");
		bbMap.put("\\[h6\\](.+?)\\[/h6\\]", "<h6>$1</h6>");
		bbMap.put("\\[quote\\](.+?)\\[/quote\\]", "<blockquote>$1</blockquote>");
		bbMap.put("\\[p\\](.+?)\\[/p\\]", "<p>$1</p>");
		bbMap.put("\\[p=(.+?),(.+?)\\](.+?)\\[/p\\]", "<p style='text-indent:$1px;line-height:$2%;'>$3</p>");
		bbMap.put("\\[center\\](.+?)\\[/center\\]", "<div align='center'>$1");
		bbMap.put("\\[align=(.+?)\\](.+?)\\[/align\\]", "<div align='$1'>$2");
		bbMap.put("\\[color=(.+?)\\](.+?)\\[/color\\]", "<span style='color:$1;'>$2</span>");
		bbMap.put("\\[size=(.+?)\\](.+?)\\[/size\\]", "<span style='font-size:$1;'>$2</span>");
		bbMap.put("\\[img\\](.+?)\\[/img\\]", "<img src='$1' />");
		bbMap.put("\\[img=(.+?),(.+?)\\](.+?)\\[/img\\]", "<img width='$1' height='$2' src='$3' />");
		bbMap.put("\\[email\\](.+?)\\[/email\\]", "<a href='mailto:$1'>$1</a>");
		bbMap.put("\\[email=(.+?)\\](.+?)\\[/email\\]", "<a href='mailto:$1'>$2</a>");
		bbMap.put("\\[url\\](.+?)\\[/url\\]", "<a href='$1'>$1</a>");
		bbMap.put("\\[url=(.+?)\\](.+?)\\[/url\\]", "<a href='$1'>$2</a>");
		bbMap.put(
			"\\[youtube\\](.+?)\\[/youtube\\]",
			"<object width='640' height='380'><param name='movie' value='http://www.youtube.com/v/$1'></param><embed src='http://www.youtube.com/v/$1' type='application/x-shockwave-flash' width='640' height='380'></embed></object>");
		bbMap.put("\\[video\\](.+?)\\[/video\\]", "<video src='$1' />");

		for (final Map.Entry<String, String> entry : bbMap.entrySet())
		{
			html = html.replaceAll(entry.getKey().toString(), entry.getValue().toString());
		}

		return Html.fromHtml(html);
	}

	public boolean hasNoJson()
	{
		if (getRomsJson() == null && getDescJson() == null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public void displayToast(final Context context, final String text)
	{
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public Bitmap downloadImage(final String URL) throws IOException
	{
		final URL url = new URL(URL);
		final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoInput(true);
		conn.connect();
		final InputStream is = conn.getInputStream();
		return BitmapFactory.decodeStream(is);
	}

	public String string(int id)
	{
		return context.getResources().getString(id);
	}

	public String getUpdateJson()
	{
		String json = null;

		try
		{
			final FileInputStream is = new FileInputStream(context.getFilesDir().getPath() + "/" + FILE3);
			final int size = is.available();
			final byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			json = new String(buffer, "UTF-8");
		}
		catch (final IOException e)
		{
			e.printStackTrace();
			return null;
		}
		return json;
	}

	public boolean isNewVersionAvailable()
	{
		try
		{
			final double version = new JSONObject(getUpdateJson()).getDouble("version");
			if (version > Double.parseDouble(string(R.string.app_version)))
			{
				return true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return false;
	}
}
