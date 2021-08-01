import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

/**
 * @Auther:liyang
 * @Date:2021/7/30 - 07 -30 -22:08
 * @Description:PACKAGE_NAME
 * @Version:1.0
 */
public class Ticket12306 implements Runnable{
    //    票
    private Integer tickets=10;
//    锁
    InterProcessMutex interProcessLock;
    public Ticket12306(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000,10);
//        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("192.168.122.1:2181", 60000, 15000, retryPolicy);
//        curatorFramework.start();
//       方法二：
        CuratorFramework curator = CuratorFrameworkFactory.builder().connectString("192.168.23.129:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(1500).
                        retryPolicy(retryPolicy).namespace("itheima").build();
        curator.start();
        interProcessLock = new InterProcessMutex(curator,"/lock");

    }
    public void run() {
        //        得到锁
        while (true){
        try {
//            失败之后等3秒
            interProcessLock.acquire(3, TimeUnit.SECONDS);
            if (tickets>0){

                System.out.println(Thread.currentThread()+"  "+tickets);
//               获得的太快了，让该线程睡3秒
                Thread.sleep(100);
                tickets--;

            }
            //       释放锁
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                interProcessLock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    }
}
