package com.androidfany.rommanager;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.androidfany.rommanager.Fragment.*;
import com.androidfany.rommanager.Helper.*;
import java.util.*;
import org.json.*;

public class MainActivity extends ActionBarActivity
{
	// Method Holder
	private final MethodHolder methodHolder = new MethodHolder(MainActivity.this);

	// Context
	public static Context context;

	// UI Stuff
	public DrawerLayout drawerLayout;
	public ListView drawer;
	private ArrayList<String> romList;
	private ArrayAdapter<String> drawerAdapter;
	private ActionBarDrawerToggle drawerToggle;

	// Reserved dialog
	private AlertDialog.Builder dialog;

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		getSupportFragmentManager().beginTransaction().add(R.id.main_view, new MainFragment()).commit();

		context = this;
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);
		drawer = (ListView) findViewById(R.id.drawer);
		drawer.setOnItemClickListener(new DrawerItemClickListener());
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close)
		{
			@Override
			public void onDrawerClosed(final View drawerView)
			{
				supportInvalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(final View drawerView)
			{
				supportInvalidateOptionsMenu();
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);

		new ParseJson().execute();
		checkUpdate();
	}

	@Override
	protected void onPostCreate(final Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		getMenuInflater().inflate(R.menu.actionbar_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		if (drawerToggle.onOptionsItemSelected(item))
		{
			return true;
		}

		final TextView romNameTextView = (TextView) findViewById(R.id.rom_fragment_romName);
		final TextView romDescTextView = (TextView) findViewById(R.id.rom_fragment_desc);
		final TextView romAuthorTextView = (TextView) findViewById(R.id.rom_fragment_romAuthor);
		final Button downloadButton = (Button) findViewById(R.id.rom_fragment_download);
		final Button visitThreadButton = (Button) findViewById(R.id.rom_fragment_visitthread);

		switch (item.getItemId())
		{
			case R.id.action_refresh:
				if (methodHolder.hasUserInternet())
				{
					new ParseJson().execute();
					supportInvalidateOptionsMenu();

					try
					{
						final JSONObject romNameJson = new JSONObject(methodHolder.getRomsJson());
						if (romNameTextView != null)
						{
							try
							{
								final String nameRom = romNameJson.getJSONArray("roms").getJSONObject(DrawerPressedPosition.pressed_pos).getString("name");
								final String versionRom = romNameJson.getJSONArray("roms").getJSONObject(DrawerPressedPosition.pressed_pos).getString("version");
								romNameTextView.setText(nameRom + " " + versionRom);
							}
							catch (Exception e)
							{
								final String nameRom = romNameJson.getJSONArray("roms").getJSONObject(DrawerPressedPosition.pressed_pos).getString("name");
								romNameTextView.setText(nameRom);
							}
						}

						if (romAuthorTextView != null)
						{
							try
							{
								final String authorRom = romNameJson.getJSONArray("roms").getJSONObject(DrawerPressedPosition.pressed_pos).getString("author");
								romAuthorTextView.setText(string(R.string.by) + " " + authorRom);
							}
							catch (Exception e)
							{}
						}

						final JSONObject romDescJson = new JSONObject(methodHolder.getDescJson());

						if (romDescTextView != null)
						{
							try
							{
								final String descRom = romDescJson.getJSONArray("desc").getJSONObject(DrawerPressedPosition.pressed_pos).getString("desc");
								romDescTextView.setText(methodHolder.bbcode(string(R.string.desc_title) + descRom));
							}
							catch (Exception e)
							{}
						}

						if (downloadButton != null && visitThreadButton != null)
						{
							downloadButton.setEnabled(true);
							visitThreadButton.setEnabled(true);
						}
					}
					catch (final Exception e)
					{
						methodHolder.displayToast(this, string(R.string.rom_not_available));
						getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new MainFragment()).commit();
						e.printStackTrace();
					}
				}
				else
				{
					dialog = new AlertDialog.Builder(this);
					dialog.setTitle(string(R.string.no_internet));
					dialog.setMessage(string(R.string.no_internet_refresh));
					dialog.setCancelable(true);
					dialog.setPositiveButton(string(R.string.ok), null);
					dialog.show();
				}
				return true;
			case R.id.action_about:
				final Intent intent = new Intent(this, AboutActivity.class);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private final class ParseJson extends AsyncTask<String, Void, Void>
	{
		private ProgressDialog waitDialog;

		@Override
		protected void onPreExecute()
		{
			waitDialog = new ProgressDialog(MainActivity.this);
			waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			waitDialog.setCancelable(false);
			waitDialog.setMessage(string(R.string.getting_data));
			waitDialog.show();
		}

		@Override
		protected Void doInBackground(final String... params)
		{
			methodHolder.updateJson();
			return null;
		}

		@Override
		protected void onPostExecute(final Void result)
		{
			waitDialog.dismiss();

			try
			{
				final JSONObject main = new JSONObject(methodHolder.getRomsJson());
				final JSONArray romsArray = main.getJSONArray("roms");
				romList = new ArrayList<String>();

				for (int i = 0; i < romsArray.length(); i++)
				{
					romList.add(romsArray.getJSONObject(i).getString("name"));
				}

				drawerAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.drawer_item_layout, R.id.drawer_item_layout_romname, romList)
				{

					@Override
					public View getView(final int position, final View convertView, final ViewGroup parent)
					{
						final View view = super.getView(position, convertView, parent);
						final TextView romName = (TextView) view.findViewById(R.id.drawer_item_layout_romname);
						final TextView romVersion = (TextView) view.findViewById(R.id.drawer_item_layout_version);
						final TextView romNew = (TextView) view.findViewById(R.id.drawer_item_layout_romnew);

						try
						{
							final JSONObject rom = romsArray.getJSONObject(position);
							final String name = rom.getString("name");
							final String version = rom.getString("version");
							boolean isNew;
							if (rom.getString("new") == "true")
							{
								isNew = true;
							}
							else
							{
								isNew = false;
							}

							romName.setText(name);
							romVersion.setText(string(R.string.version) + ": " + version);

							if (!isNew)
							{
								romNew.setVisibility(View.INVISIBLE);
							}
						}
						catch (final Exception e)
						{
							romNew.setVisibility(View.INVISIBLE);
							e.printStackTrace();
						}
						return view;
					}
				};

				drawerAdapter.sort(new Comparator<String>()
					{
						@Override
						public int compare(final String s1, final String s2)
						{
							return s1.compareTo(s2);
						}
					});
				drawer.setAdapter(drawerAdapter);
			}
			catch (final Exception e)
			{
				dialog = new AlertDialog.Builder(MainActivity.this);
				dialog.setTitle(string(R.string.parsing_failed));
				dialog.setMessage(string(R.string.parsing_failed_desc));
				dialog.setPositiveButton(string(R.string.ok), new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(final DialogInterface dialog, final int which)
						{
							finish();
						}
					});
				dialog.setCancelable(false);
				dialog.show();
				e.printStackTrace();
			}
		}
	}

	private String string(int id)
	{
		return methodHolder.string(id);
	}

	private void checkUpdate()
	{
		if (methodHolder.isNewVersionAvailable())
		{
			dialog = new AlertDialog.Builder(this);
			dialog.setTitle(string(R.string.update_title));
			dialog.setMessage(string(R.string.update_desc));
			dialog.setCancelable(true);
			dialog.setPositiveButton(string(R.string.yes), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int pos)
					{
						try
						{
							final String updateURL = new JSONObject(methodHolder.getUpdateJson()).getString("download");
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(Uri.parse(updateURL));
							startActivity(intent);
						}
						catch (Exception e)
						{
							methodHolder.displayToast(MainActivity.this, string(R.string.update_error));
							e.printStackTrace();
						}
					}
				});
			dialog.setNeutralButton(string(R.string.view_changelog), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						final String changeLog;
						try
						{
							changeLog = new JSONObject(methodHolder.getUpdateJson()).getString("changelog");
						}
						catch (Exception e)
						{
							changeLog = string(R.string.unknown_changelog);
							e.printStackTrace();
						}
						dialog.create().dismiss();
						AlertDialog.Builder tempDialog = new AlertDialog.Builder(MainActivity.this);
						tempDialog.setView(LayoutInflater.from(MainActivity.this).inflate(R.layout.changelog_layout, null));
						tempDialog.setTitle(methodHolder.bbcode(string(R.string.changelog_bbcode)));
						tempDialog.setMessage(methodHolder.bbcode(changeLog));
						tempDialog.setPositiveButton(string(R.string.ok), new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									checkUpdate();
								}
							});
						tempDialog.setCancelable(true);
						tempDialog.show();
					}
				});
			dialog.setNegativeButton(string(R.string.later), null);
			dialog.show();
		}
	}
}
