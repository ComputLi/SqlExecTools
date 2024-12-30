环境需求：java11
使用方法：
	java -jar SqlExecTools.jar
注意事项：
    sql输入应当以分号结尾; 如果需要执行sql脚本，则以@fileName执行，当前版本以分号为sql结束符进行判定，因此不支持函数以及存储过程，仅支持
    基本的增删查改操作
	参数文件放置在jar包中，默认加载当前路径的jdbc包，支持oracle、mysql、DB2、达梦数据库，如果需要添加其他数据库，则需要修改配置文件
	1 解压配置文件 jar xf SqlExecTools.jar Configuration.json (当前路径存在Configuration.json时，会使用外部json)
	2 在json文件中配置所需添加的数据库，以及jar包的路径，修改完成后保存退出即可
	    2.1 添加数据库时，url中的通配符应当与parameterName中的提示名、数量一一对应，防止界面产生误解
	    2.2 jarPath为jdbc包的存放路径，默认空为当前jar包运行路径，注意windows路径中为两个反斜杠\\
	    2.3 如果数据库需要添加多个jar包，则jarPackage值应为["xxxx1.jar", "xxxx2.jar"]