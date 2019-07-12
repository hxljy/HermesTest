
#

>  由于Android不同进程之前不能相互通信，所以当开发过程中遇到跨进程通信的时候,常用的方案就是AIDL(Android Interface Definition Language)通过它我们可以定义进程间的通信接口,但是当应用中出现大量跨进程通信的时候，比如你想体验一下插件化开发或者特殊需求在单应用中需要开多个进程，那么写过AIDL的同学都会有痛不欲生的感觉。现在福利来了，可以试试饿了么开源了一款进程间事件分发的库—[HermesEventBus](https://github.com/eleme/HermesEventBus)。
           在介绍HermesEventBus之前先简单介绍一下它底层依赖的库Hermes—-同样是由饿了么Android资深工程师赵立飞操刀的一套新颖巧妙易用的Android进程间通信IPC框架,开发Hermes的初衷是为了解决插件化框架DroidPlugin的主从进程通信困难的问题,最后实现的效果是将进程间通信变的像调用本地函数一样方便简单，并且支持进程间函数回调和垃圾回收。
          想了解更多，请移步飞神的[Hermes](https://github.com/Xiaofei-it/Hermes)。
---


## 一、Hermes实现核心思想

1. **aidl**
android中跨进程通讯，通常是使用aidl了,但是aidl使用起来，相对会比较麻烦，因为每次都要自己写一个aidl文件，然后创建service，在service中返回binder进行通讯。

2. **动态代理**
在客户端可以提供一个接口，然后创建动态代理，每次调用方法时，获取到方法名，然后通过aidl跨进程调用，在服务端，再通过方法名和classid反射调用对应的方法。

---

## 二、封装使用

设置服务端MainActivity和客户端SecondActivity在不同进程

```java
        <activity android:name="com.hxl.hermes.MainActivity">
        </activity>

        <activity android:name="com.hxl.hermes.SecondActivity"
            android:process=":second"/>
```

#### 1.创建跨进程单例

在服务端进程和客户端进程中必须要有一个完全相同的接口。该接口主要提供给客户端进程使用。

服务端接口类上面要加上注解 @ClassId("实现类的全路径")

```java
@ClassId("com.hxl.hermes.dao.UserManager")
public interface IUserManager {

    Person getPerson();
    void setPerson(Person person);

}

服务端进程中要有一个单例类实现该接口，实现类必须是单例的，而且获取单例方法名必须是：*getInstance()*

``````java
public class UserManager implements IUserManager{
    private static final UserManager ourInstance = new UserManager();

    public static UserManager getInstance() {
        return ourInstance;
    }

    private UserManager() {
    }

    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

}
```

#### 2.服务端注册


```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Hermes.getDefault().register(UserManager.class);
        Hermes.getDefault().register(FileManager.class);
    }
```


给单例设置要传递的值
```java
 UserManager.getInstance().setPerson(new Person("hxl", "123456"));
```

#### 3.客户端连接

```java
 Hermes.getDefault().connect(this, HermesService.class);
```


#### 4.跨进程通讯，客户端获取服务端单例

```java
        IUserManager iUserManager = Hermes.getDefault().getInstance(IUserManager.class);
        Person person = iUserManager.getPerson();
        Toast.makeText(this, person.toString(), Toast.LENGTH_SHORT).show();
```

---

## 三、核心代码

#### 1.服务端注册

服务端注册后会将单例存到缓存中

```java
    public void register(Class<?> clazz) {
        typeCenter.register(clazz);
    }


    public void register(Class<?> clazz){
        registerClass(clazz);
        registerMethod(clazz);
    }
```

```java
    //注册类的类名
    private final ConcurrentHashMap<String, Class<?>> mAnnotatedClasses;
    //注册的所有方法
    private final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, Method>> mRawMethods;

    private void registerMethod(Class<?> clazz){
        mRawMethods.putIfAbsent(clazz, new ConcurrentHashMap<String, Method>());
        ConcurrentHashMap<String, Method> map = mRawMethods.get(clazz);

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            String key = TypeUtils.getMethodId(method);
            map.put(key, method);
        }
    }

    private void registerClass(Class<?> clazz){
        String className = clazz.getName();
        mAnnotatedClasses.putIfAbsent(className, clazz);
    }

```


## 2.客户端连接

```java
    public void connect(Context context, Class<? extends HermesService> service){
        connectApp(context, null, service);
    }

    private void connectApp(Context context, String packageName, Class<? extends HermesService> service){
        serviceConnectionManager.bind(context.getApplicationContext(), packageName, service);
    }
```

绑定服务端HermesService，获取binder

```java
    public void bind(Context context, String packageName, Class<? extends HermesService> service){
        HermesServiceConnection connection = new HermesServiceConnection(service);
        mHermesServiceConnections.put(service, connection);
        Intent intent;
        if (TextUtils.isEmpty(packageName)){
            intent = new Intent(context, service);
        }else{
            intent = new Intent();
            intent.setClassName(packageName,service.getName());
        }
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }


        private class HermesServiceConnection implements ServiceConnection{

            private Class<? extends HermesService> mClass;

            public HermesServiceConnection(Class<? extends HermesService> mClass) {
                this.mClass = mClass;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                SunService sunService = SunService.Stub.asInterface(service);//获取binder
                mHermesServices.put(mClass, sunService);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mHermesServices.remove(mClass);
            }
        }
```

## 3.跨进程通讯

客户端获取服务端单例时通过动态代理

```java
    IUserManager iUserManager = Hermes.getDefault().getInstance(IUserManager.class);
```

```java
    public <T> T getInstance(Class<T> clazz){
        return getProxy(HermesService.class, clazz);
    }

    private <T> T getProxy(Class<? extends HermesService> service, Class clazz){
        ClassLoader classLoader = service.getClassLoader();
        return (T) Proxy.newProxyInstance(classLoader, new Class<?>[]{clazz},
                new HermesInvocationHandler(service, clazz));
    }
```

当客户端通过动态代理调用单例方法时，会响应invoke方法，如下：

```java
public class HermesInvocationHandler implements InvocationHandler {

    private Class clazz;
    private static Gson gson = new Gson();
    private Class hermesService;

    public HermesInvocationHandler(Class<? extends HermesService> service, Class clazz){
        this.clazz = clazz;
        this.hermesService = service;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Response responce= Hermes.getDefault().sendObjectRequest(hermesService, clazz, method, args); //1
        if (!TextUtils.isEmpty(responce.getData())){
            ResponseBean responceBean = gson.fromJson(responce.getData(), ResponseBean.class);
            if (responceBean.getData() != null){
                String data = gson.toJson(responceBean.getData());
                Class<?> returnType = method.getReturnType();
                return gson.fromJson(data, returnType);
            }
        }
        return null;
    }
}
```
代码1会根据单例类名、调用方法名封装成request对象，如下：

```java
    public <T> Response sendObjectRequest(Class<? extends HermesService> hermesServiceClass, Class<T> clazz, Method method, Object[] parameters) {
        RequestBean requestBean = new RequestBean();
        //获取class name
        ClassId classId = clazz.getAnnotation(ClassId.class);
        //是否加注解
        if (classId == null){
            requestBean.setClassName(clazz.getName());
            requestBean.setResultClassName(clazz.getName());
        }else{
            requestBean.setClassName(classId.value());
            requestBean.setResultClassName(classId.value());
        }

        //设置方法名
        if (method != null){
            requestBean.setMethodName(TypeUtils.getMethodId(method));
        }

        //设置参数信息，将参数都json化
        RequestParameter[] requestParameters = null;
        if (parameters != null && parameters.length > 0){
            requestParameters = new RequestParameter[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Object parameter = parameters[i];
                String parameterClassName = parameter.getClass().getName();
                String parameterValue = gson.toJson(parameter);

                RequestParameter requestParameter = new RequestParameter(parameterClassName, parameterValue);
                requestParameters[i] = requestParameter;
            }
        }
        if (requestParameters != null){
            requestBean.setRequestParameters(requestParameters);
        }

        //封装为Request对象
        Request request = new Request(gson.toJson(requestBean), TYPE_GET);
        return serviceConnectionManager.request(hermesServiceClass, request);//1
    }
...

	//serviceConnectionManager.request
    public Response request(Class<? extends HermesService> sunHermesServiceClass, Request request){
        SunService sunService = mHermesServices.get(sunHermesServiceClass);//获取缓存的binder
        if (sunService != null){
            try {
                return sunService.send(request);//通过binder调用服务端的send
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
```

服务端接收到信息后，生成响应对象
```java
public class HermesService extends Service{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private SunService.Stub mBinder = new SunService.Stub() {
        @Override
        public Response send(Request request)  {
            ResponseMake responseMake = null;
            switch (request.getType()){
                case Hermes.TYPE_GET:
                    //获取单例
                    responseMake = new InstanceResponseMake();
                    break;
            }
            if (responseMake != null){
                return responseMake.makeResponse(request);//根据request生成response
            }
            return null;
        }
    };
}
```

生成response对象 如下：

```java
    public Response makeResponse(Request request){
        //1. 取出request中的requestBean消息并转换为requestBean。
        RequestBean requestBean = gson.fromJson(request.getData(), RequestBean.class);

        //2. 通过requestBean中设置的目标单例类的名字去加载类。
        resultClass = typeCenter.getClassType(requestBean.getResultClassName());

        //3. 通过requestBean中的设置的方法名获取到要执行的具体方法。
        setMethod(requestBean);//1

        //4. 组装参数，将参数进行还原组装。
        RequestParameter[] requestParameters = requestBean.getRequestParameters();
        if (requestParameters != null && requestParameters.length > 0){
            mParameters = new Object[requestParameters.length];
            for (int i = 0; i < requestParameters.length; i++) {
                RequestParameter requestParameter = requestParameters[i];
                Class<?> clazz = typeCenter.getClassType(requestParameter.getParameterClassName());
                mParameters[i] = gson.fromJson(requestParameter.getParameterValue(), clazz);
            }
        }else{
            mParameters = new Object[0];
        }

        //5. 执行方法，并得到执行结果
        Object resultObj = invokeMethod();//2

        //6. 将执行结果封装为Response返回给客户端进程
        ResponseBean responseBean = new ResponseBean(resultObj);
        return new Response(gson.toJson(responseBean));
    }
```

代码1,2在InstanceResponseMake中实现，

```java
    @Override
    protected void setMethod(RequestBean requestBean) {
        try {
            instance = objectCenter.get(requestBean.getClassName());
            if (instance == null){//为获取到单例对象，需要先获取
            	//服务端单例的方法名必须为getInstance
                Method getInstanceMethod = resultClass.getMethod("getInstance", new Class[]{});
                if (getInstanceMethod != null){
                    instance = getInstanceMethod.invoke(null);//获取单例对象
                    objectCenter.put(instance);//缓存起来
                }
            }
			//获取到request中的方法名
            mMethod = typeCenter.getMethod(resultClass, requestBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Object invokeMethod() {
        Object object = null;
        try {
        	//最终通过反射，调用服务端方法
            object = mMethod.invoke(instance, mParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

```








