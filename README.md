# PageRouter
android 页面路由

```
目前只支持 单 module

```
类型声明错误会提示
```
注: AptLogger>>>end<<<
错误: AptLoggerAn exception is encountered, [buildPutDoc - the @Args field "stringList" in TestActivity Bundle类型不支持: java.util.List<java.lang.String>]
错误: AptLoggerAn exception is encountered, [buildPutDoc - the @Args field "stringList" in TestActivity Bundle类型不支持: java.util.List<java.lang.String>]
注: AptLogger>>>end<<<
```

use 

```
// 以 router 方式启动（页面配置了 path ）
    PageRouter
        .of(this)
        .path("test")
        .setParams(new TestActivityBundle.Builder().setMsg("ssss").bundle())
        .setUri(Uri.parse(""))
        .navigation();
// 以 launch 方式启动

   new Test1ActivityBundle.Builder("Welcome2Test1Activity")
                               .setName('z')
                               .setMoney(50000)
                               .setBeanS(new SerializableBean("SerializableBean"))
                               .setBeanP(new ParcelableBean("ParcelableBean"))
                               .setStringArrayList(as)
                               .setIntegerArrayList(ai)
                               .setBm(bm)
                               .launch()        

```
不设计成 setParams("key","value") 就是为了能明确看见进入的 Activity 传入哪些参数

注意声明的类型
```
不支持没有实现 Parcelable or Serializable 的普通对象
//@Args（require = true)
//public NormalBean normalBean;
field 前的类型必须写为 ArrayList
require 为true 为页面必须传的参数 会在 BundleBuilder 的构造方法要求传入
//@Args
//public List<String> stringList;
@Args
public ArrayList<String> stringList;

```

test page

```
@Route(path = "test")
public class TestActivity extends AppCompatActivity {

    @Args
    public String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        BundleService.bind(this);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }
}

```
generator 生成文件 com.sohu.generate 下

```
public final class RouterDispatcher {

    public static void go(Context ctx,String path,Bundle extras, Uri uri){ 
        if(ctx==null)return;
        Intent intent = null;
        switch (path){
            case "test":
               intent = new Intent(ctx, TestActivity.class);
            break;

        }
        if (intent == null) return;
        if (extras != null) {
            intent.putExtras(extras);
        }
        if (!(ctx instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (uri != null){
            intent.setData(uri);
        }
        ctx.startActivity(intent);

    }

}

public final class Test1ActivityBundle {


    public static final class Builder {

        private final Bundle args;
        private Uri uri;
        public Builder(java.lang.String msg) {
            this.args = new Bundle();
            args.putString("msg",msg);
        }

        public Test1ActivityBundle.Builder setName(java.lang.Character name){
            args.putChar("name",name);
            return this;
        }
        public Test1ActivityBundle.Builder setMoney(double money){
            args.putDouble("money",money);
            return this;
        }
        public Test1ActivityBundle.Builder setBeanS(com.woaiqw.pagerouter.bean.SerializableBean beanS){
            args.putSerializable("beanS",beanS);
            return this;
        }
        public Test1ActivityBundle.Builder setBeanP(com.woaiqw.pagerouter.bean.ParcelableBean beanP){
            args.putParcelable("beanP",beanP);
            return this;
        }
        public Test1ActivityBundle.Builder setBm(android.graphics.Bitmap bm){
            args.putParcelable("bm",bm);
            return this;
        }
        public Test1ActivityBundle.Builder setStringArrayList(java.util.ArrayList<java.lang.String> stringArrayList){
            args.putStringArrayList("stringArrayList",stringArrayList);
            return this;
        }
        public Test1ActivityBundle.Builder setIntegerArrayList(java.util.ArrayList<java.lang.Integer> integerArrayList){
            args.putIntegerArrayList("integerArrayList",integerArrayList);
            return this;
        }
        public Test1ActivityBundle.Builder withUri(Uri uri){
            this.uri = uri;
            return this;
        }

        public void lunch(Context ctx) {
            if (ctx==null)return;
            Intent intent = new Intent(ctx,Test1Activity.class);
            if (!(ctx instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            if (uri!=null){
                intent.setData(uri);
            }
            if(args!=null){
                intent.putExtras(args);
            }
            ctx.startActivity(intent);
        }
        public void lunch(Context ctx,Class clazz) {
            if (ctx==null)return;
            Intent intent = new Intent(ctx,clazz);
            if (!(ctx instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            if (uri!=null){
                intent.setData(uri);
            }
            if(args!=null){
                intent.putExtras(args);
            }
            ctx.startActivity(intent);
        }
        public Bundle bundle() {
            return args;
        }

    }

    public static void bind(Test1Activity target) {
        Intent intent = target.getIntent();
        if (intent==null)return;
        Bundle source = intent.getExtras();
        if (source==null)return;
        if (source.containsKey("msg")) {
            target.msg = (java.lang.String) source.getString("msg");
        } else {
            throw new IllegalStateException("msg is required, but not found in the bundle.");
        }
        if (source.containsKey("name")) {
            target.name = (java.lang.Character) source.getChar("name");
        }
        if (source.containsKey("money")) {
            target.money = (double) source.getDouble("money");
        }
        if (source.containsKey("beanS")) {
            target.beanS = (com.woaiqw.pagerouter.bean.SerializableBean) source.getSerializable("beanS");
        }
        if (source.containsKey("beanP")) {
            target.beanP = (com.woaiqw.pagerouter.bean.ParcelableBean) source.getParcelable("beanP");
        }
        if (source.containsKey("bm")) {
            target.bm = (android.graphics.Bitmap) source.getParcelable("bm");
        }
        if (source.containsKey("stringArrayList")) {
            target.stringArrayList = (java.util.ArrayList<java.lang.String>) source.getStringArrayList("stringArrayList");
        }
        if (source.containsKey("integerArrayList")) {
            target.integerArrayList = (java.util.ArrayList<java.lang.Integer>) source.getIntegerArrayList("integerArrayList");
        }

    }
}

```

