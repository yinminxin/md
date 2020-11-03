# Nginx

## 拦截非法请求，转发统一错误页面

1. 在server域中加入一下代码

   ```shell
   if ($request_method !~* GET|POST) { return 444; }
       #使用444错误代码可以更加减轻服务器负载压力。
       #防止SQL注入
       if ($query_string ~* (\$|'|--|[+|(%20)]union[+|(%20)]|[+|(%20)]insert[+|(%20)]|[+|(%20)]drop[+|(%20)]|[+|(%20)]truncate[+|(%20)]|[+|(%20)]update[+|(%20)]|[+|(%20)]from[+|(%20)]|[+|(%20)]grant[+|(%20)]|[+|(%20)]exec[+|(%20)]|[+|(%20)]where[+|(%20)]|[+|(%20)]select[+|(%20)]|[+|(%20)]and[+|(%20)]|[+|(%20)]or[+|(%20)]|[+|(%20)]count[+|(%20)]|[+|(%20)]exec[+|(%20)]|[+|(%20)]chr[+|(%20)]|[+|(%20)]mid[+|(%20)]|[+|(%20)]like[+|(%20)]|[+|(%20)]iframe[+|(%20)]|[\<|%3c]script[\>|%3e]|javascript|alert|webscan|dbappsecurity|style|confirm\(|innerhtml|innertext)(.*)$) { return 555; }
       if ($uri ~* (/~).*) { return 501; }
       if ($uri ~* (\\x.)) { return 501; }
       #防止SQL注入   
      if ($query_string ~* "[;'<>].*") { return 509; }
        if ($request_uri ~ " ") { return 509; }
    if ($request_uri ~ (\/\.+)) { return 509; }
    if ($request_uri ~ (\.+\/)) { return 509; }
    
    #if ($uri ~* (insert|select|delete|update|count|master|truncate|declare|exec|\*|\')(.*)$ ) { return 503; }
    #防止SQL注入
    if ($request_uri ~* "(cost\()|(concat\()") { return 504; }
    if ($request_uri ~* "[+|(%20)]union[+|(%20)]") { return 504; }
    if ($request_uri ~* "[+|(%20)]and[+|(%20)]") { return 504; }
    if ($request_uri ~* "[+|(%20)]select[+|(%20)]") { return 504; }
    if ($request_uri ~* "[+|(%20)]or[+|(%20)]") { return 504; }
    if ($request_uri ~* "[+|(%20)]delete[+|(%20)]") { return 504; }
    if ($request_uri ~* "[+|(%20)]update[+|(%20)]") { return 504; }
    if ($request_uri ~* "[+|(%20)]insert[+|(%20)]") { return 504; }
    if ($query_string ~ "(<|%3C).*script.*(>|%3E)") { return 505; }
    if ($query_string ~ "GLOBALS(=|\[|\%[0-9A-Z]{0,2})") { return 505; }
    if ($query_string ~ "_REQUEST(=|\[|\%[0-9A-Z]{0,2})") { return 505; }
    if ($query_string ~ "proc/self/environ") { return 505; }
    if ($query_string ~ "mosConfig_[a-zA-Z_]{1,21}(=|\%3D)") { return 505; }
    if ($query_string ~ "base64_(en|de)code\(.*\)") { return 505; }
    if ($query_string ~ "[a-zA-Z0-9_]=http://") { return 506; }
    if ($query_string ~ "[a-zA-Z0-9_]=(\.\.//?)+") { return 506; }
    if ($query_string ~ "[a-zA-Z0-9_]=/([a-z0-9_.]//?)+") { return 506; }
    if ($query_string ~ "b(ultram|unicauca|valium|viagra|vicodin|xanax|ypxaieo)b") { return 507; }
    if ($query_string ~ "b(erections|hoodia|huronriveracres|impotence|levitra|libido)b") {return 507; }
    if ($query_string ~ "b(ambien|bluespill|cialis|cocaine|ejaculation|erectile)b") { return 507; }
    if ($query_string ~ "b(lipitor|phentermin|pro[sz]ac|sandyauer|tramadol|troyhamby)b") { return 507; }
    #这里大家根据自己情况添加删减上述判断参数，cURL、wget这类的屏蔽有点儿极端了，但要“宁可错杀一千，不可放过一个”。
    if ($http_user_agent ~* YisouSpider|ApacheBench|WebBench|Jmeter|JoeDog|Havij|GetRight|TurnitinBot|GrabNet|masscan|mail2000|github|wget|curl|Java|python) { return 508; }
    #同上，大家根据自己站点实际情况来添加删减下面的屏蔽拦截参数。
    if ($http_user_agent ~* "Go-Ahead-Got-It") { return 508; }
    if ($http_user_agent ~* "GetWeb!") { return 508; }
    if ($http_user_agent ~* "Go!Zilla") { return 508; }
    if ($http_user_agent ~* "Download Demon") { return 508; }
    if ($http_user_agent ~* "Indy Library") { return 508; }
    if ($http_user_agent ~* "libwww-perl") { return 508; }
    if ($http_user_agent ~* "Nmap Scripting Engine") { return 508; }
    if ($http_user_agent ~* "~17ce.com") { return 508; }
    if ($http_user_agent ~* "WebBench*") { return 508; }
        if ($http_user_agent ~* "spider") { return 508; } #这个会影响国内某些搜索引擎爬虫，比如：搜狗
        
        
    #错误代码转发
    error_page 500 501 502 503 504 505 506 507 508 509 555 404 444 /404.html;
       location = /404.html{
    root  /usr/share/nginx/html/err;
       }

   #错误页面图片转发
       location = /img/404.png{
           root  /usr/share/nginx/html/err;
       }

   ```

2. 在http域中加入下面两行代码

   ```shell
   fastcgi_intercept_errors on;
       proxy_intercept_errors on;
       
       add_header X-Frame-Options "SAMEORIGIN";   
       add_header X-XSS-Protection "1; mode=block";    
       add_header X-Content-Type-Options "nosniff";
   ```

3. 错误页面文件位置[D:\yinminxin\资料\资料\yinminxin\nginx_conf\err]()


## Nginx隐藏版本信息

1. 查看Nginx版本信息

   ```shell
   curl -I [IP]
   ```

2. 隐藏版本信息,进入nginx/conf

   2.2 修改nginx.conf

   ```shell
   #在http{}里面加上server_tokens off;
   http {
       ……省略
       sendfile on;
       tcp_nopush on;
       keepalive_timeout 60;
       tcp_nodelay on;
       server_tokens off;
       …….省略
   }
   ```

   2.3 修改fastcgi.conf或fcgi.conf

   ```shell
   找到：
   fastcgi_param SERVER_SOFTWARE nginx/$nginx_version;
   改为：
   fastcgi_param SERVER_SOFTWARE nginx;
   ```

3. 重新加载Nginx配置

   ```shell
   nginx -s reload
   ```

   ​