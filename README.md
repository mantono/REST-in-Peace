# REST-in-Peace
Simple Java server backend for a REST API.

## Using REST-in-Peace
Setting up and using REST-in-Peace for serving requests in a restfull manner is very easy and requires little time and configuration.
### Define Resources
Use the *Resource* annotation and enter the URI as the argument for the annotation. Note the _%id_ parameter within the URI, and in the argument for the method.
See the [DummyClass](https://github.com/mantono/REST-in-Peace/blob/master/test/com/mantono/webserver/reflection/DummyClass.java) for more examples of how to setup your resources.
> This is essentially all that is required to serve a web page as a response for an URI.

```
@Resource("/path/%id")
public static Response myMethod(final int id)
{
  return new WebPage("<html><body>Hello "+id+"!<body></html>");
}
```
The method must be public and static, but has no limitation on naming conventions (beyond those of Java). The number of parameters in URI (in other words, the number of occurences of the `%` sign) must match the number of parameters in the method. Names of the respective parameters does not matter, only the order.
The return type of the method must always be of type *Response*.
Response itself is an interface, but is simple enough to be implmentend by anyone.
```
public interface Response
{
	ResponseCode getResponseCode();
	Header getHeader();
	CharSequence getBody();
}
```

Here, simplicity and freedom is chosen over complex solutions which may or may not suit your needs. In other words, it's up to you to implement the class.
For serving simpler web pages as respone, an existing class can be used, [WebPage](https://github.com/mantono/REST-in-Peace/blob/master/src/com/mantono/webserver/WebPage.java).

By default, all Resources are considered to be available through *GET*, unless stated otherwise. To define a resource as something else of the common verbs, use the Resource annotation that takes an additional argument.
In the example below, we define a resource available through POST instead of GET.
```
@Resource(verb = Verb.POST, value = "/test/%user/%token")
public static Response testOfPost(final String user, final String token)
{
  return new WebPage("<html><body>Output:<p><b>"+user+"</b></p><p><em>"+token+"</b></em><body></html>");
}
```

### Starting REST-in-Peace

Call method [Server.start()](https://github.com/mantono/REST-in-Peace/blob/master/src/com/mantono/webserver/Server.java) from a class in your project. All class files in your project's classpath will be searched for any methods with the Resource annotation.
