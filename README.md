# PageRouter
android 页面路由

目前只支持 单 module

use 

```
    PageRouter
        .of(this)
        .path("test")
        .setParams(new TestActivityBundle.Builder().setMsg("ssss").bundle())
        .setUri(Uri.parse(""))
        .navigation();

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

public final class TestActivityBundle {


    public static final class Builder {

        private final Bundle args;
        
        public Builder() {
            this.args = new Bundle();
        }

        public TestActivityBundle.Builder setMsg(String msg){
            args.putString("msg",msg);
            return this;
        }
        public Bundle bundle() {
            return args;
        }

    }
    public static void bind(TestActivity target) {
        Intent intent = target.getIntent();
        if (intent==null)return;
        Bundle source = intent.getExtras();
        if (source==null)return;
       if (source.containsKey("msg")) {
            target.msg = (String) source.getString("msg");
        } else {
            throw new IllegalStateException("msg is required, but not found in the bundle.");
        }
    }
}

```

