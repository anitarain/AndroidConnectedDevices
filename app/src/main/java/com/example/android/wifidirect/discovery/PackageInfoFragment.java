package com.example.android.wifidirect.discovery;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anitaimani on 09/02/16.
 */
public class PackageInfoFragment extends ListActivity {

    private ListView lView;
    private ArrayList results = new ArrayList();
    public String name = "";
    public String packageName = "";
    public String versionName = "";
    public int versionCode = 0;
    public Drawable icon;
    public View l1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.package_list);

        //l1 = findViewById(R.id.Layout1);
        lView = (ListView) findViewById(android.R.id.list);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                lauchApplication();
            }
        });
        PackageManager pm = this.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        // intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED);
        for (ResolveInfo rInfo : list) {
            results.add(rInfo.activityInfo.applicationInfo.loadLabel(pm).toString());
            Log.w("Installed Applications", rInfo.activityInfo.applicationInfo.loadLabel(pm).toString());
        }
        lView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, results));
    }





 //       public ArrayList<Pinfo> getInstalledApps(boolean getSysPackages) {
//            ArrayList<Pinfo> res = new ArrayList<Pinfo>();
//            List<PackageInfo> packs = this.getPackageManager().getInstalledPackages(0);
//            for(int i=0;i<packs.size();i++) {
//                PackageInfo p = packs.get(i);
//                if ((!getSysPackages) && (p.versionName == null)) {
//                    continue ;
//                }





//
//                  PackageManager packageManager = this.getPackageManager();
            //        List<PackageInfo> packages = packageManager.getInstalledPackages(0);
            //
            //        List<Pinfo> myPackages = new ArrayList<Pinfo>();
//          for (PackageInfo pack : packages) {
//                if ((!getSysPackages) && (pack.versionName == null)){
//                    continue;
//                }
//            Pinfo newPack = new Pinfo();
//            newPack.setPackageName(pack.packageName);
//            newPack.setName(pack.applicationInfo.loadLabel(packageManager)
//                    .toString());
//            //newPack.setIcon(pack.applicationInfo.loadIcon(packageManager));
//            icon = rInfo.activityInfo.applicationInfo.loadIcon(pm);
//
//            myPackages.add(newPack);
//        }
//       lView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, myPackages));
//    }


//        @Override
//        protected void onListItemClick(ListView l, View v, int position, long id)
//        {
//            String selection = l.getItemAtPosition(position).toString();
//            Toast.makeText(this, selection, Toast.LENGTH_LONG).show();
//            super.onListItemClick(l, v, position, id);
//        }


    protected void lauchApplication()
    {
        //Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage();
        //startActivity( LaunchIntent );
        Intent LaunchIntent = new Intent(Intent.ACTION_MAIN, null);
        LaunchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        startActivity(LaunchIntent );
    }


}


