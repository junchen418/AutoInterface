使用场景
===
设定一个json串：
```
{
    "retCode": "200",
    "retMsg": "success",
    "data": [
        {
            "id": 1,
            "name": "test",
            "date": "2017-01-09 13:30:00"
        },
        {
            "id": 2,
            "name": "test1",
            "date": "2017-01-09 13:40:00"
        }
    ]
}
```
如果想要获取以上json串的所有"name"的值，对于正常解析，你得遍历，但对于zson，你只需要这样：
```
ZsonResult zr = ZSON.parseJson(json);
List<Object> names = zr.getValues("//name");
```
我们在进行结果断言时，有时候请求返回的一整个json串作为一个期望值来进行断言，但json串中往往会存在有不固定的值，比如上面json串的"date"，每次都是变化的，这样就不好断言了，于是，在zson中，我们可以把这个date的值进行更改，改成一个固定的值:
```
ZsonResult zr = ZSON.parseJson(json);
zr.updateValue("//date","0000-00-00 00:00:00");
```
或者干脆删除这个结点:
```
ZsonResult zr = ZSON.parseJson(json);
zr.deleteValue("//date");
```
以上zson对json串的操作包含了查找，更新，删除。zson还有对json串中增加一个子字符串的操作:
```
ZsonResult zr = ZSON.parseJson(json);
zr.addValue("/data",2,"{\"id\":3,\"name\":\"test2\",\"date\":\"2017-01-09 14:30:00\"}");

####使用说明

```
Zson z = new Zson(); //new一个Zson对象

ZsonResult zr = z.parseJson(j); //解析JSON字符串后，得到一个ZsonResult对象
```

>     zr对象可用的方法:

```
Object getValue(String path) //返回一个除了List或Map的Object对象，如果是List或Map，会转换成为JSON字符串返回

Map<String, Object> getMap(String path) //返回一个Map对象

List<Object> getList(String path) //返回一个List对象

String toJsonString(Object obj) //将Map或List转换成为JSON字符串
```

***

####选择器path说明

示例一:

```
[
    {
        "firstName": "Eric",
        "lastName": "Clapton",
        "instrument": "guitar"
    },
    {
        "firstName": "Sergei",
        "lastName": "Rachmaninoff",
        "instrument": "piano"
    }
]
```

>     找出第二个firstName: /*[1]/firstName 
>     输出:Sergei
***
>     找出第一个Map: /*[0]  
>     输出:{"firstName": "Eric","lastName": "Clapton","instrument": "guitar"}


示例二:

`{"a":["a"],"cb":{"a":1},"d":["a",{"a":[1,2]},{"a":2},""],"e":"b"}`

>     路径: /d/*[1]/a 
>     输出:[1,2]
***
>     路径: /d/*[1]/a/*[0]
>     输出:1

***