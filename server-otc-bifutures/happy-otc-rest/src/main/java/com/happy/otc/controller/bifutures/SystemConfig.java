package com.happy.otc.controller.bifutures;

import com.happy.otc.dao.BiFuturesParamMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Administrator on 2018\11\19 0019.
 */
public class SystemConfig implements Serializable ,Runnable,ApplicationContextAware,InitializingBean {
    private static final long serialVersionUID = 4034185684084165163L;

    private final static String VERSION_FIELD = "VERSION";

    private static ApplicationContext ctx;

    private static final int DEFAULT_REFRESH_INTERVAL = 10;

    private static volatile Map<String, String> param = new ConcurrentHashMap<String, String>(48,0.75f,32);
    private static final ExecutorService exec = Executors.newSingleThreadExecutor();
    private static final AtomicLong lastLoad = new AtomicLong(0);

    private static volatile String  version=null;

    /**
     * 刷新间隔秒
     */
    private static int refreshInterval;

    /**
     * 资源文件位置
     */
    private static String resourceLocation;

    private Thread refreshWorker = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(SystemConfig.refreshInterval<=0){
            SystemConfig.refreshInterval = DEFAULT_REFRESH_INTERVAL;
        }
    }

    @Override
    public void run() {

        while(!Thread.interrupted()){
            try {
                checkAndRefresh();
                TimeUnit.SECONDS.sleep(refreshInterval);
            } catch (InterruptedException e) {
                break;
            } catch(Exception e){
            }
        }
    }

    @PostConstruct
    public void init(){
        if(refreshWorker==null){
            refreshWorker = new Thread(this);
            refreshWorker.start();
        }
        lastLoad.set(System.currentTimeMillis());
        reload();
    }
    /**
     * 通过参数名获取字符类型的参数
     *
     * @param name
     * @return
     */
    public static String get(String name){
        return name==null?null:param.get(name);
    }
    /**
     * 通过名称获取值,不存在则返回默认值
     */
    public static String get(String name,String def){
        String val = get(name);
        return val==null?def:val;
    }
    /**
     * 通过名称获取int类型的参数
     */
    public static Integer getInt(String name){
        String value = get(name);
        if(value!=null){
            Integer v = null;
            try {
                v = Integer.parseInt(value);
            } catch (NumberFormatException e) {
            }
            return v;
        }
        return null;
    }
    /**
     * 获取int类型的参数,没有则返回默认值

     */
    public static int getInt(String name,int def){
        Integer v = getInt(name);
        return v==null?def:v;
    }
    /**
     * 获取double类型的参数，没有则返回<code>null</code>
     */
    public static Double getDouble(String name){
        String value = get(name);
        if(value!=null){
            Double v = null;
            try {
                v = Double.parseDouble(value);
            } catch (NumberFormatException e) {
            }
            return v;
        }
        return null;
    }
    /**
     * 获取double类型的参数，没有则返回默认值
     */
    public static double getDouble(String name,Double def){
        Double v = getDouble(name);
        return v==null?def:v;
    }
    /**
     * 获取boolean类型变量
     */
    public static Boolean getBoolean(String name) {
        String value = get(name);
        if (value != null) {
            Boolean v = null;
            try {
                v = Boolean.parseBoolean(value);
            } catch (Exception e) {
            }
            return v;
        }
        return null;
    }

    /**
     * 获取Boolean类型的参数
     */
    public static boolean getBoolean(String name,boolean def){
        Boolean v = getBoolean(name);
        return v==null?def:v;
    }
    /**
     * 检查并刷新缓存
     */
    protected static void checkAndRefresh(){
        if(System.currentTimeMillis()-lastLoad.get()>refreshInterval*1000){
            exec.submit((Callable<Void>) () -> {
                lastLoad.set(System.currentTimeMillis());
                if(isVersionChanged()){
                    reload();
                }
                return null;
            });
        }
    }
    /**
     * 修改系统参数

     */
    public static boolean updateConfig(String key,String value){
        /*BiFuturesParamMapper paramDAO = ctx.getBean(BiFuturesParamMapper.class);
        if(paramDAO!=null){
            Param p = paramDAO.getByName(StringUtils.trimToEmpty(key));
            if(p!=null){
                Param mod = new Param(p.getId());
                mod.setValue(StringUtils.trimToEmpty(value));
                boolean flag =  paramDAO.update(mod)==1;
                if(flag){
                    Param v = paramDAO.getByName(VERSION_FIELD);
                    Param vmod = new Param(v.getId());
                    vmod.setValue(DateUtil.format(new Date(), "HHmmss"));
                    if(paramDAO.update(vmod)==1){
                        param.put(StringUtils.trimToEmpty(key), StringUtils.trimToEmpty(value));
                    }
                }
                return flag;
            }
        }*/
        return false;
    }
    /**
     * 加载数据
     */
    protected static void reload(){
        loadDbParam();
    }

    /**
     * 加载数据库的参数
     *
     * @return
     */
    protected static void loadDbParam(){
      /*  BiFuturesParamMapper paramDAO = ctx.getBean(BiFuturesParamMapper.class);
        List<Param> list = paramDAO.getAll();
        if(!list.isEmpty()){
            for(Param p:list){
                param.put(StringUtils.trimToEmpty(p.getName()), StringUtils.trimToEmpty(p.getValue()));
            }
        }*/
    }
    public static boolean isVersionChanged(){
        BiFuturesParamMapper paramDAO = ctx.getBean(BiFuturesParamMapper.class);
       /* if(paramDAO!=null){
            Param p = paramDAO.getByName(VERSION_FIELD);
            boolean changed = p!=null&&!p.getValue().equals(version);
            if(changed){
                version = p.getValue();
            }
            return changed;
        }*/
        return false;
    }
    /**
     * 关闭刷新任务
     */
    public void destroy(){
        if(refreshWorker!=null){
            refreshWorker.interrupt();
        }
        exec.shutdownNow();
    }
    public String getResourceLocation() {
        return resourceLocation;
    }

    public void setResourceLocation(String resourceLocation) {
        SystemConfig.resourceLocation = resourceLocation;
    }

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) {
        SystemConfig.refreshInterval = refreshInterval;
    }
}
