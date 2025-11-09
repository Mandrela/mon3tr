/** Data Base compatability layer. */
package su.maibat.mon3tr.db;

/*
Database layout

Deadlines:
ID  | Burn Date | Name  | Remind offset | UserID    | GroupID   | Active[0|1]



User:
ID  | ChatId    | Deadline Query limit  | HPSFWN    | Active[-1|0|1]
                  32                                  Banned|Activen't|Active


Groups:
ID  | List of UserIDs
*/
