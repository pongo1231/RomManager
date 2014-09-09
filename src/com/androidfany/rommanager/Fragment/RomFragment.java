package com.androidfany.rommanager.Fragment;

import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.text.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.androidfany.rommanager.*;
import com.androidfany.rommanager.Helper.*;
import org.json.*;

public class RomFragment extends Fragment
{
	MethodHolder methodHolder = new MethodHolder(MainActivity.context);
	private int drawer_pressed_pos;
	private TextView romName;
	private TextView romAuthor;
	private TextView romDesc;
	private static ScrollView root;
	private Button threadButton;
	private Button downloadButton;
	private ImageView imageLeft;
	private ImageView imageRight;
	private RelativeLayout imagePack;
	private View strut;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		drawer_pressed_pos = getArguments().getInt("DRAWER_PRESSED_POS");
		root = (ScrollView) inflater.inflate(R.layout.rom_fragment_layout, container, false);
		romName = (TextView) root.findViewById(R.id.rom_fragment_romName);
		romAuthor = (TextView) root.findViewById(R.id.rom_fragment_romAuthor);
		romDesc = (TextView) root.findViewById(R.id.rom_fragment_desc);
		threadButton = (Button) root.findViewById(R.id.rom_fragment_visitthread);
		threadButton.setOnClickListener(new OnButtonClick());
		downloadButton = (Button) root.findViewById(R.id.rom_fragment_download);
		downloadButton.setOnClickListener(new OnButtonClick());
		imageLeft = (ImageView) root.findViewById(R.id.rom_fragment_image1);
		imageRight = (ImageView) root.findViewById(R.id.rom_fragment_image2);
		imagePack = (RelativeLayout) root.findViewById(R.id.imagepack);
		strut = root.findViewById(R.id.strut);

		if (!methodHolder.hasUserInternet())
		{
			threadButton.setEnabled(false);
			downloadButton.setEnabled(false);
		}
		return root;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		new ParseJson().execute();
	}

	private final class OnButtonClick implements OnClickListener
	{
		@Override
		public void onClick(final View view)
		{
			switch (view.getId())
			{
				case R.id.rom_fragment_visitthread:
					try
					{
						final String visitThreadUrl = new JSONObject(methodHolder.getRomsJson()).getJSONArray("roms")
							.getJSONObject(DrawerPressedPosition.pressed_pos).getString("threadurl");
						final Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(visitThreadUrl));
						startActivity(intent);

					}
					catch (final Exception e)
					{
						threadButton.setEnabled(false);
						methodHolder.displayToast(getActivity(), string(R.string.something_went_wrong));
						e.printStackTrace();
					}
					break;
				case R.id.rom_fragment_download:
					try
					{
						final String downloadUrl = new JSONObject(methodHolder.getRomsJson()).getJSONArray("roms").getJSONObject(DrawerPressedPosition.pressed_pos)
							.getString("downloadurl");
						final Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(downloadUrl));
						startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(downloadUrl)));
						break;

					}
					catch (final Exception e)
					{
						downloadButton.setEnabled(false);
						methodHolder.displayToast(getActivity(), string(R.string.something_went_wrong));
						e.printStackTrace();
					}
			}
		}
	}

	private final class ParseJson extends AsyncTask<Object, Object, Object>
	{
		String romNameString = null;
		String romAuthorString = null;
		String romDescString = null;

		@Override
		protected Object doInBackground(final Object... object)
		{
			JSONObject descJson = null;
			JSONArray descArray = null;
			JSONObject descObject = null;

			new ParseJsonBitmap().execute();

			try
			{
				descJson = new JSONObject(methodHolder.getDescJson());
				descArray = descJson.getJSONArray("desc");
				descObject = descArray.getJSONObject(drawer_pressed_pos);

			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}

			JSONObject romJson = null;
			JSONArray romArray = null;
			JSONObject romObject = null;

			try
			{
				romJson = new JSONObject(methodHolder.getRomsJson());
				romArray = romJson.getJSONArray("roms");
				romObject = romArray.getJSONObject(drawer_pressed_pos);
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
			try
			{
				romDescString = descObject.getString("desc");
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
			try
			{
				romObject = romArray.getJSONObject(drawer_pressed_pos);
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
			try
			{
				romNameString = romObject.getString("name") + " " + romObject.getString("version");
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
			try
			{
				romAuthorString = string(R.string.by) + " " + romObject.getString("author");
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Object result)
		{
			super.onPostExecute(result);
			if (romDescString != null || !"".equals(romDescString))
			{
				romDesc.setText(methodHolder.bbcode(string(R.string.desc_title) + romDescString));
			}
			else
			{
				romDesc.setText(string(R.string.unknown_desc));
			}
			if (romNameString != null || !"".equals(romNameString))
			{
				romName.setText(romNameString);
			}
			else
			{
				romName.setText(string(R.string.unknown_rom));
			}
			if (romAuthorString != null || !"".equals(romAuthorString))
			{
				romAuthor.setText(romAuthorString);
			}
			else
			{
				romAuthor.setText(string(R.string.unknown_author));
			}
		}
	}

	private class ParseJsonBitmap extends AsyncTask<Boolean, Void, Boolean>
	{
		Bitmap leftBitmap;
		Bitmap rightBitmap;

		@Override
		protected Boolean doInBackground(final Boolean... arg0)
		{
			try
			{
				final String leftBitmapURL = new JSONObject(methodHolder.getRomsJson()).getJSONArray("roms").getJSONObject(drawer_pressed_pos)
					.getString("image1");
				leftBitmap = methodHolder.downloadImage(leftBitmapURL);

				final String rightBitmapURL = new JSONObject(methodHolder.getRomsJson()).getJSONArray("roms").getJSONObject(drawer_pressed_pos)
					.getString("image2");
				rightBitmap = methodHolder.downloadImage(rightBitmapURL);
				return true;
			}
			catch (final Exception e)
			{
				return false;
			}
		}

		@Override
		protected void onPostExecute(final Boolean success)
		{
			super.onPostExecute(success);
			if (success)
			{
				imageLeft.setImageBitmap(leftBitmap);
				imageRight.setImageBitmap(rightBitmap);
			}
			else
			{
				imageLeft.setVisibility(View.GONE);
				imageRight.setVisibility(View.GONE);
				strut.setVisibility(View.GONE);
				imagePack.setVisibility(View.GONE);
				root.invalidate();
			}
		}
	}

	private String string(int id)
	{
		return methodHolder.string(id);
	}
}
