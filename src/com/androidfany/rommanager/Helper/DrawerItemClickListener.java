package com.androidfany.rommanager.Helper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.androidfany.rommanager.MainActivity;
import com.androidfany.rommanager.R;
import com.androidfany.rommanager.Fragment.RomFragment;

public class DrawerItemClickListener implements OnItemClickListener
{
	private final MainActivity activity = (MainActivity) MainActivity.context;
	private final MethodHolder methodHolder = new MethodHolder(MainActivity.context);
	private AlertDialog.Builder dialog;

	@Override
	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id)
	{
		if (!methodHolder.hasNoJson())
		{
			final FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
			final RomFragment f = new RomFragment();
			final Bundle bundle = new Bundle();
			new DrawerPressedPosition(position);

			bundle.putInt("DRAWER_PRESSED_POS", position);
			f.setArguments(bundle);
			ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
			ft.replace(R.id.main_view, f);
			ft.commit();

			activity.drawerLayout.closeDrawer(activity.drawer);
		}
		else if (methodHolder.hasUserInternet())
		{
			dialog = new AlertDialog.Builder(activity);
			dialog.setTitle("Please refresh!");
			dialog.setMessage("Please refresh from the options!");
			dialog.setCancelable(true);
			dialog.setPositiveButton("Ok", null).show();
		}
		else
		{
			dialog = new AlertDialog.Builder(activity);
			dialog.setTitle("No Internet Connection");
			dialog.setMessage("You have no internet connection, neither a cached version of the files. Please activate internet and re-launch this application!");
			dialog.setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(final DialogInterface dialog, final int which)
				{
					activity.finish();
				}
			});
			dialog.show();
		}
	}
}
