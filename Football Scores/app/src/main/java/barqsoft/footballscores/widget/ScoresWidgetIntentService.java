package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;
import barqsoft.footballscores.data.DatabaseContract;

/**
 * Created by Bruno on 02-07-2015.
 */
public class ScoresWidgetIntentService extends IntentService {

    public ScoresWidgetIntentService() {
        super("ScoresWidgetIntentService");
    }

    private static final String[] SCORES_COLUMNS = {
            DatabaseContract.scores_table.LEAGUE_COL,
            DatabaseContract.scores_table.DATE_COL,
            DatabaseContract.scores_table.TIME_COL,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.MATCH_ID,
            DatabaseContract.scores_table.MATCH_DAY
    };
    // these indices must match the projection
    private static final int INDEX_LEAGUE_COL = 0;
    private static final int INDEX_DATE_COL = 1;
    private static final int INDEX_TIME_COL = 2;
    private static final int INDEX_HOME_COL = 3;
    private static final int INDEX_AWAY_COL = 4;
    private static final int INDEX_HOME_GOALS_COL = 5;
    private static final int INDEX_AWAY_GOALS_COL = 6;
    private static final int INDEX_MATCH_ID = 7;
    private static final int INDEX_MATCH_DAY = 8;

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, ScoresWidgetProvider.class));


        Uri todayUri = DatabaseContract.scores_table.buildScoreWithDate();
        Cursor data = getContentResolver().query(todayUri, SCORES_COLUMNS, null, new String[]{Utilities.formatDate(System.currentTimeMillis())}, null);



        boolean hasData = (data != null) && data.moveToFirst();


        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views;

            if (hasData) {
                // Find the correct layout based on the widget's width
                int layoutId;
                int widgetWidth = getWidgetWidth(appWidgetManager, appWidgetId);
                int defaultWidth = getResources().getDimensionPixelSize(R.dimen.widget_default_width);
                int largeWidth = getResources().getDimensionPixelSize(R.dimen.widget_large_width);
                if (widgetWidth >= largeWidth) {
                    layoutId = R.layout.widget_large;
                } else {
                    layoutId = R.layout.widget_small;
                }
                  views = new RemoteViews(getPackageName(), layoutId);

                // Add the data to the RemoteViews
                if (widgetWidth >= largeWidth) {
                    views.setImageViewResource(R.id.home_crest, Utilities.getTeamCrestByTeamName(data.getString(INDEX_HOME_COL)));
                    views.setImageViewResource(R.id.away_crest, Utilities.getTeamCrestByTeamName(data.getString(INDEX_AWAY_COL)));
                    views.setTextViewText(R.id.data_textview, data.getString(INDEX_TIME_COL));
                }


                views.setTextViewText(R.id.home_name, data.getString(INDEX_HOME_COL));
                views.setTextViewText(R.id.away_name, data.getString(INDEX_AWAY_COL));
                views.setTextViewText(R.id.score_textview, Utilities.getScores(data.getInt(INDEX_HOME_GOALS_COL), data.getInt(INDEX_AWAY_GOALS_COL)));


                // Content Descriptions for RemoteViews were only added in ICS MR1
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, "todo: description");
                }
                // Create an Intent to launch MainActivity
                Intent launchIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
                views.setOnClickPendingIntent(R.id.widget, pendingIntent);


            }else{
                views = new RemoteViews(getPackageName(),  R.layout.widget_small);
                views.setTextViewText(R.id.home_name, getString(R.string.empty_list));
                views.setTextViewText(R.id.away_name,  "");
                views.setTextViewText(R.id.score_textview, "");
            }
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        data.close();//TODO:por tudo em vars??

    }

    private int getWidgetWidth(AppWidgetManager appWidgetManager, int appWidgetId) {
        // Prior to Jelly Bean, widgets were always their default size
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return getResources().getDimensionPixelSize(R.dimen.widget_default_width);
        }
        // For Jelly Bean and higher devices, widgets can be resized - the current size can be
        // retrieved from the newly added App Widget Options
        return getWidgetWidthFromOptions(appWidgetManager, appWidgetId);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private int getWidgetWidthFromOptions(AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
            int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            // The width returned is in dp, but we'll convert it to pixels to match the other widths
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidthDp,
                    displayMetrics);
        }
        return getResources().getDimensionPixelSize(R.dimen.widget_default_width);
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.home_crest, description);
    }
}
