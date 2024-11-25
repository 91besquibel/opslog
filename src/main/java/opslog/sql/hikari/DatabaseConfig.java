package opslog.sql.hikari;

public class DatabaseConfig {

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
            "id, date, time, typeID, tagIDs, initials, description";

    public static final String CALENDAR_TABLE =
            "calendar_table";
    public static final String CALENDAR_COLUMNS =
            "id, title, start_date, stop_date, start_time, stop_time, typeID, tagIDs, initials, description";

    public static final String SCHEDULED_CHECKLIST_TABLE =
            "scheduled_checklist_table";
    public static final String SCHEDULED_CHECKLIST_COLUMNS =
            "id, checklist_id, start_date, stop_date, offsets, durations, status_list, percentage";
}
