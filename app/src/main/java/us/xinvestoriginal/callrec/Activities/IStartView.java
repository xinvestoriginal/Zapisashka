package us.xinvestoriginal.callrec.Activities;

/**
 * Created by x-inv on 06.05.2017.
 */

public interface IStartView {
    void close();
    void loadPermissions();
    void onPermissionObtain();
    void onNoPermissions();
    boolean hasPermission(String permission);
}
