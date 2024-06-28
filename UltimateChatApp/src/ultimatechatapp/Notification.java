package ultimatechatapp;
 //Notification Thread
    public class Notification implements Runnable {

        int count = 0;
        String friend = "";

        @Override
        public void run() {
            while (true) {
                count = list1.getItemCount();
                for (int i = 0; i < count; i++) {
                    friend = list1.getItem(i);
                    try {
                        dos = new DataOutputStream(socket.getOutputStream());
                        dos.writeUTF("CMD_NOTIFICATION " + friend + " " + username);

                        Thread.sleep(1900);
                    } catch (Exception e) {
                    }

                }

            }

        }
    }