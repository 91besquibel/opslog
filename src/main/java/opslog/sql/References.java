package opslog.sql;

public class References {

    public static final String TAG_TABLE =
            "tag_table";
    public static final String TAG_COLUMN =
            "id, title, color";

    public static final String TYPE_TABLE =
            "type_table";
    public static final String TYPE_COLUMN =
            "id, title, pattern";

    public static final String TASK_TABLE =
            "task_table";
    public static final String TASK_COLUMN =
            "id, title, typeID, tagIDs, initials, description";

    public static final String FORMAT_TABLE =
            "format_table";
    public static final String FORMAT_COLUMN =
            "id, title, format";

    public static final String PROFILE_TABLE =
            "profile_table";
    public static final String PROFILE_COLUMN =
            "id, title, root_color, primary_color, secondary_color, border_color, text_color, text_size, text_font";

    public static final String PINBOARD_TABLE =
            "pinboard_table";
    public static final String PINBOARD_COLUMN =
            "id, date, time, typeID, tagIDs, initials, description";

    public static final String CHECKLIST_TABLE =
            "checklist_table";
    public static final String CHECKLIST_COLUMNS =
            "id, title, task_list, typeid, tagids, initials, description";

    public static final String LOG_TABLE =
            "log_table";
    public static final String LOG_COLUMN =
            "id, date, time, typeid, tagids, initials, description";

    public static final String SCHEDULED_EVENT_TABLE =
            "scheduled_event_table";
    public static final String SCHEDULED_EVENT_COLUMNS =
            "id, start_date, stop_date, start_time, stop_time, full_day, recurrance_rule, title, location, typeID, tagIDs, initials, description";

    public static final String SCHEDULED_TASK_TABLE =
            "scheduled_task_table";
    public static final String SCHEDULED_TASK_COLUMNS =
            "id, fid, start_date, stop_date, start_time, stop_time, full_day, recurrance_rule, completion_status, title, location, typeID, tagIDs, initials, description";

    public static final String START_DATE_COLUMN_TITLE = "start_date";
    public static final String STOP_DATE_COLUMN_TITLE = "stop_date";
    public static final String DATE_COLUMN_TITLE ="date";
    public static final String TYPE_COLUMN_TITLE = "typeID";
    public static final String TAG_COLUMN_TITLE = "tagIDs";
    public static final String INITIALS_COLUMN_TITLE = "initials";
    public static final String DESCRIPTION_COLUMN_TITLE = "description";

    public static String [] threadNames(){
        return new String[]{
                LOG_TABLE,
                PINBOARD_TABLE,
                CHECKLIST_TABLE,
                SCHEDULED_TASK_TABLE,
                TASK_TABLE,
                SCHEDULED_EVENT_TABLE,
                FORMAT_TABLE,
                TAG_TABLE,
                TYPE_TABLE,
                PROFILE_TABLE,
        };
    }
}
