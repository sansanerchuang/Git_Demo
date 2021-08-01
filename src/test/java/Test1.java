import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @Auther:liyang
 * @Date:2021/7/30 - 07 -30 -14:26
 * @Description:PACKAGE_NAME
 * @Version:1.0
 */

public class Test1 {
    CuratorFramework curator;
    @Before
    public void test1(){
//方法一
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000,10);
//        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("192.168.122.1:2181", 60000, 15000, retryPolicy);
//        curatorFramework.start();
//       方法二：
        curator = CuratorFrameworkFactory.builder().connectString("192.168.23.129:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(1500).
                        retryPolicy(retryPolicy).namespace("itheima").build();
        curator.start();
//        namespace这个方法使用在build方法之前。
//        curator.start();
    }
//    创建节点
//    创建带数据的节点
//    创建不同类型的节点
//    创建不同多级节点

    @Test
    public void test2() throws Exception {
        String s = curator.create().creatingParentsIfNeeded().forPath("/app4/p1", "itheima".getBytes());
        System.out.println(s);

    }
//------------------------------------------------------------------------------------------------------------
//   查询命令：设置了名称空间之后，所有的值都素是名称空间中进行操作的。
//    查询数据 get path
//    查询子节点 ls  path
//     查询状态  ls2 : ls -s
//    查询数据
    @Test
    public  void testGet1() throws Exception {

        byte[] bytes = curator.getData().forPath("/app1");
        String s = new String(bytes);
        System.out.println(s);
    }

    @Test
    public  void testGet2() throws Exception {
//        拿到/itheima下的子节点。
        List<String> strings = curator.getChildren().forPath("/");
        System.out.println(strings);
    }
    @Test
    public  void testGet3() throws Exception {
        Stat stat = new Stat();
        System.out.println(stat);
        curator.getData().storingStatIn(stat).forPath("/app1");
        System.out.println(stat);
    }

    //-----------------------------------------------------------------------
    //直接修改，不专业。
    @Test
    public  void testSet1() throws Exception {
        curator.setData().forPath("/app1","itheima".getBytes());

    }


    //-----------------------------------------------------------------------
//    根据版本号修改，保证在我操作的期间，没有人操作数据
    @Test
    public  void testSet2() throws Exception {
        Stat stat = new Stat();
        curator.getData().storingStatIn(stat).forPath("/app1");
        System.out.println(stat.getVersion());
        curator.setData().withVersion(10).forPath("/app1","hehe".getBytes());
    }

//---------------------------------------delete-----------------------------------------------
//    删除
//    delete all
//    必须成功的删除 guaranteed
//    回调函数
    @Test
    public void testDelete() throws Exception {
        Void aVoid = curator.delete().forPath("/app1");
        System.out.println(aVoid);
    }
    @Test
    public void testDelete2() throws Exception {
        curator.delete().deletingChildrenIfNeeded().forPath("/app4");
    }
    @Test
    public void testDelete3() throws Exception {
        curator.delete().guaranteed().deletingChildrenIfNeeded().forPath("/app2");
    }
    @Test
    public void testDelete4() throws Exception {
         curator.delete().guaranteed().deletingChildrenIfNeeded().inBackground(
                 new BackgroundCallback() {
                     public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                         System.out.println("删除成功");
                         System.out.println(curatorEvent);
                     }
                 }
         ).forPath("/app4");
    }



    @After
    public void test3(){
        if (curator!=null){
            curator.close();
        }
    }
// 创建watch监听器：
        @Test
    public void ListenerWatcherTes1() throws Exception {
//            创建监听器对象
       final   NodeCache nodeCache = new NodeCache(curator,"/app1");
//       2.注册监听器
        nodeCache.getListenable().addListener(new NodeCacheListener() {

            public void nodeChanged() throws Exception {
                ChildData currentData = nodeCache.getCurrentData();
                System.out.println("节点改变");
                System.out.println(currentData);
            }
        });

//        开启监听器：接受参数，如果为true,就家中缓存数据到nodeCache中。
            nodeCache.start();
// 方法持续监听
            while (true){


            }
        }



//       创建子节点监听器

        @Test
    public void ListenerTes2() throws Exception {
    //            创建监听器对象
            final PathChildrenCache pathChildrenCache = new PathChildrenCache(curator,"/app1",true);
//            注册监听器：
            pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                    System.out.println("节点变化了");
                    System.out.println(pathChildrenCacheEvent);
//                    我们监听数据的变化，
                    PathChildrenCacheEvent.Type type = pathChildrenCacheEvent.getType();
                    if(PathChildrenCacheEvent.Type.CHILD_UPDATED.equals(type)){
                        System.out.println("数据变更了");
//                        拿到数据
                        byte[] data = pathChildrenCacheEvent.getData().getData();
                        String s = new String(data);
                        System.out.println(s);
                    }
                }
            });
//            启动监听
            pathChildrenCache.start();

            while (true){

            }
        }

        @Test
    public void ListenerTes3() throws Exception {
    //            创建监听器对象
            TreeCache treeCache=new TreeCache(curator,"/app2");

//            注册监听器
            treeCache.getListenable().addListener(
                    new TreeCacheListener() {
                        public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                            System.out.println("节点变化了");
                            System.out.println(treeCacheEvent);
                        }
                    }
            );

            treeCache.start();
            while (true){

            }
        }
}
