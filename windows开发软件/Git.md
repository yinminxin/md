# Git

## Git下载与安装

### 直接官网下载 : [官网](https://git-scm.com/downloads)

### 安装

除了下面图片这里手动选择“use git from xxx”以外，其他的都默认，一路next即可

![2019061611592768](link\Git\img\2019061611592768.png)

## Git配置

### 配置环境变量

```
#在path中添加git的bin目录路径
D:\yinminxin\mysoft\git\Git\bin
```

### 配置全局的用户名和邮箱

在桌面右键-【Git Bash Here】

![20190616121300465](link\Git\img\20190616121300465.png)

![20190616121500733](link\Git\img\20190616121500733.png)

输入命令

```shell
git config --global user.name "yinminxin"
git config --global user.email "yinminxin@wondersgroup.com"
```

## Git常用命令

```
常用命令：
git add:将本地文件增加到暂存区
git commit:将暂存区的内容 提交到 本地仓库（本地分支，默认master分支）
git push:将本地仓库内容 推送到 远程仓库（远程分支）
git pull：将远程仓库（远程分支）内容 拉取到 本地仓库（本地分支）
git branch :查看所有分支
git checkout xxx:切换分支到xxx
git checkout xxx -f：强制切换分支到xxx
git branch xxx：创建分支
git checkout -b xxx：创建并切换到xxx分支
git branch -d xxx：删除xxx分支
git branch -D xxx：强制删除xxx分支
```

## gitignore修改流程

1. 修改本地gitignore文件

2. 本地与远程代码要一致(pull&push操作)

3. 运行下面的代码：

   ```shell
   git rm -r --cached .
   git add .
   git commit -m 'edit gitignore文件'
   git push -u origin master
   ```

## git首次提交本地代码到远程

1. 初始化本地`git`仓库

2. 提交代码到本地`git`仓库

3. 本地仓库连接远程仓库地址

4. 将代码提交远程

   ```shell
   $ git init
   $ git add .
   $ git commit -m 'init'
   $ git remote add origin 你的远程git地址
   $ git push -u origin master
   ```

