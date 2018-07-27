Test case for HHH-12835.

When executing the test with `-da` JVM setting to prevent Java language `assert` it works, but it fails when enabling them with `-ea`.

```
$ mvn test -DargLine="-da"
...

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 2.768 s
[INFO] Finished at: 2018-07-27T13:53:42+02:00
[INFO] Final Memory: 12M/300M
[INFO] ------------------------------------------------------------------------

```

```
$ mvn test -DargLine="-ea"
...

Tests run: 1, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 1.469 sec <<< FAILURE!
paddedBatchFetchTest(org.hibernate.bugs.PaddedBatchFetchTestCase)  Time elapsed: 0.321 sec  <<< FAILURE!
java.lang.AssertionError
	at org.hibernate.engine.internal.BatchFetchQueueHelper.removeNotFoundBatchLoadableEntityKeys(BatchFetchQueueHelper.java:61)
	at org.hibernate.loader.entity.BatchingEntityLoader.doBatchLoad(BatchingEntityLoader.java:104)
	at org.hibernate.loader.entity.PaddedBatchingEntityLoaderBuilder$PaddedBatchingEntityLoader.load(PaddedBatchingEntityLoaderBuilder.java:126)
	at org.hibernate.persister.entity.AbstractEntityPersister.load(AbstractEntityPersister.java:4265)
	at org.hibernate.event.internal.DefaultLoadEventListener.loadFromDatasource(DefaultLoadEventListener.java:509)
	at org.hibernate.event.internal.DefaultLoadEventListener.doLoad(DefaultLoadEventListener.java:479)
	at org.hibernate.event.internal.DefaultLoadEventListener.load(DefaultLoadEventListener.java:220)
	at org.hibernate.event.internal.DefaultLoadEventListener.doOnLoad(DefaultLoadEventListener.java:117)
	at org.hibernate.event.internal.DefaultLoadEventListener.onLoad(DefaultLoadEventListener.java:90)
	at org.hibernate.internal.SessionImpl.fireLoad(SessionImpl.java:1257)
	at org.hibernate.internal.SessionImpl.immediateLoad(SessionImpl.java:1115)
	at org.hibernate.proxy.AbstractLazyInitializer.initialize(AbstractLazyInitializer.java:168)
	at org.hibernate.proxy.AbstractLazyInitializer.getImplementation(AbstractLazyInitializer.java:272)
	at org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor.intercept(ByteBuddyInterceptor.java:52)
	at org.hibernate.proxy.ProxyConfiguration$InterceptorDispatcher.intercept(ProxyConfiguration.java:95)
	at org.hibernate.bugs.model.Country$HibernateProxy$lwvXdnWw.getName(Unknown Source)
	at org.hibernate.bugs.PaddedBatchFetchTestCase.paddedBatchFetchTest(PaddedBatchFetchTestCase.java:60)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at org.hibernate.testing.junit4.ExtendedFrameworkMethod.invokeExplosively(ExtendedFrameworkMethod.java:45)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:26)
	at org.junit.internal.runners.statements.RunAfters.evaluate(RunAfters.java:27)
	at org.junit.internal.runners.statements.FailOnTimeout$CallableStatement.call(FailOnTimeout.java:298)
	at org.junit.internal.runners.statements.FailOnTimeout$CallableStatement.call(FailOnTimeout.java:292)
	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
	at java.lang.Thread.run(Thread.java:748)


Results :

Failed tests:   paddedBatchFetchTest(org.hibernate.bugs.PaddedBatchFetchTestCase)

Tests run: 1, Failures: 1, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 2.828 s
[INFO] Finished at: 2018-07-27T13:55:51+02:00
[INFO] Final Memory: 12M/300M
[INFO] ------------------------------------------------------------------------

```
