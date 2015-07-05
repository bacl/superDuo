package barqsoft.footballscores;

import android.content.Context;

import java.text.SimpleDateFormat;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilities {
    public static final int SERIE_A = 357;
    public static final int PREMIER_LEGAUE = 354;
    public static final int CHAMPIONS_LEAGUE = 362;
    public static final int PRIMERA_DIVISION = 358;
    public static final int BUNDESLIGA = 351;

    public static String getLeague(Context ctx, int league_num) {
        switch (league_num) {
            case SERIE_A:
                return ctx.getString(R.string.league_serie_a);
            case PREMIER_LEGAUE:
                return ctx.getString(R.string.league_premier_league);
            case CHAMPIONS_LEAGUE:
                return ctx.getString(R.string.league_uefa_champ);
            case PRIMERA_DIVISION:
                return ctx.getString(R.string.league_primera_div);
            case BUNDESLIGA:
                return ctx.getString(R.string.league_bundesliga);
            default:
                return ctx.getString(R.string.league_unkown_leage);
        }
    }

    public static String getMatchDay(Context ctx, int match_day, int league_num) {
        if (league_num == CHAMPIONS_LEAGUE) {
            if (match_day <= 6) {
                return ctx.getString(R.string.match_day_champions_league_group_stages)+ ctx.getString(R.string.match_day_matchday, String.valueOf(match_day))  ;
            } else if (match_day == 7 || match_day == 8) {
                return ctx.getString(R.string.match_day_champions_league_first_knockout);
            } else if (match_day == 9 || match_day == 10) {
                return ctx.getString(R.string.match_day_champions_league_quarter_final);
            } else if (match_day == 11 || match_day == 12) {
                return ctx.getString(R.string.match_day_champions_league_semi_final);
            } else {
                return ctx.getString(R.string.match_day_champions_league_final);
            }
        } else {
            return ctx.getString(R.string.match_day_matchday, String.valueOf(match_day))   ;
        }
    }

    public static String getScores(int home_goals, int awaygoals) {
        if (home_goals < 0 || awaygoals < 0) {
            return " - ";
        } else {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static int getTeamCrestByTeamName(String teamname) {
        if (teamname == null) {
            return R.drawable.no_icon;
        }
        /**
         * There is no need to do this.
         * Just add a library to take care of loading teams crest from the url
         */
        switch (teamname) {
            case "Arsenal London FC":
                return R.drawable.arsenal;
            case "Manchester United FC":
                return R.drawable.manchester_united;
            case "Swansea City":
                return R.drawable.swansea_city_afc;
            case "Leicester City":
                return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC":
                return R.drawable.everton_fc_logo1;
            case "West Ham United FC":
                return R.drawable.west_ham;
            case "Tottenham Hotspur FC":
                return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion":
                return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC":
                return R.drawable.sunderland;
            case "Stoke City FC":
                return R.drawable.stoke_city;
            default:
                return R.drawable.no_icon;
        }
    }
    public static String formatDate(long dateMs) {
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        return mformat.format(dateMs);
    }
}