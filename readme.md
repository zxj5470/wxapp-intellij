# wxapp Plugin
[![JetBrains plugins](https://img.shields.io/jetbrains/plugin/v/12539-wxapp-support.svg)](https://plugins.jetbrains.com/plugin/12539-wxapp-support)
[![JetBrains plugins](https://img.shields.io/jetbrains/plugin/d/12539-wxapp-support.svg)](https://plugins.jetbrains.com/plugin/12539-wxapp-support)
<table>
  <tr>
    <th>CI</th>
    <th>Status</th>
  </tr>
  <tr>
    <td>AppVeyor (on Windows)</td>
    <td><a href="https://ci.appveyor.com/project/zxj5470/wxapp-intellij"><img src="https://ci.appveyor.com/api/projects/status/github/zxj5470/wxapp-intellij?branch=master&svg=true" alt="AppVeyor Build status"></a></td>
  </tr>
</table>

预览新版本, 见 https://ci.appveyor.com/project/zxj5470/wxapp-intellij/build/artifacts/

## 额外的配置
### 代码缩进 
另外请安装 editorconfig 插件（2018及以后的 IDEA-U 是内置了，但是 PhpStorm 和 WebStorm 我不确定有没有内置）

这个是用来保证代码缩进 2 个空格的。

配置见 [.editorconfig](https://github.com/zxj5470/wildfire-wxapp/blob/master/.editorconfig)

### 代码智能提示
（功能做出来了，不需要额外配置wx.d.ts了，直接打开一个project就能添加）

好了，那没事了

![](https://user-images.githubusercontent.com/20026798/59198181-ac79ea80-8bc5-11e9-9512-3240faeaefaf.png)

## 插件相关内容

### 兼容性：
- 支持 IntelliJ IDEA Ultimate, PhpStorm, PyCharm, Rider, RubyMine, WebStorm, AppCode, CLion, GoLand
- 有关CSS部分跳转的内容最低兼容到172（虽然我写的最低是162版本可安装）
- 172到182版本可能会报错（内嵌的加载wx.d.ts的功能未完成所以可能会报错但完全不影响使用）
- 183及以上完全正常使用

### 插件功能及相关解决方案
- js
	- 跳转：上面【好了，那没事了】那张图，ctrl+左键 点击 url 的字符串内容可跳转到对于 .js。。。
		- 甚至你写 `/pages/index/index` 也是支持跳转的，当然了不建议各位这么写
	- 内嵌了 [Matchmaker](https://github.com/lypeer/Matchmaker) 插件的功能。
		- Alt + Insert 或者光标右键 Generate... | Make match
		- 并且修改为以当前光标位置插入相应函数。那个插件居然是直接在 `Page({` 后面插入，这太鬼畜了……
	- 对于分号的标黄处理：
	![](https://user-images.githubusercontent.com/20026798/59234137-fd1d3200-8c1d-11e9-93dc-682a69237cdc.png)
- wxml 跳转
	- `{{ identifier }}`中的跳转到变量
	- `bind***="函数名"` 跳转到对应函数
	- `class='cls'` 跳转到 `当前.wxss` 或 `app.wxss`
	- 关闭标签内容为空的背景标黄问题 Alt+Enter：
	![](https://user-images.githubusercontent.com/20026798/59233969-5042b500-8c1d-11e9-9b54-08a4cfee8fd8.png)
		
- wxss
	- rpx 问题：
		- 解决rpx底下红色 “报错” 标注以及代码格式化 Ctrl(cmd)+ Alt(option) + L 后 数字与rpx之间出现空格的问题
		
		![](https://user-images.githubusercontent.com/20026798/59233255-4a97a000-8c1a-11e9-819f-e648f7ea1ef0.png)
		- Alt + Enter，关闭检查。
		
		![](https://user-images.githubusercontent.com/20026798/59233270-58e5bc00-8c1a-11e9-9d81-736709fe2633.png)
		- 显示结果：
		
		![](https://user-images.githubusercontent.com/20026798/59233421-f214d280-8c1a-11e9-843f-57a498e8e248.png)
	

## Pending……
- 内嵌能用的 wx.d.ts 声明文件
- wxml缩进
- wxss的class内容跳转到wxml（估计做不了，我不会，但是如果用不优雅的方法倒是可以实现，我就不恶心自己了）
- 另外理论上说完全可以实现做到实时预览。（谁去实现？）

## 效果图
### WXML
![](https://user-images.githubusercontent.com/20026798/59234019-82541700-8c1d-11e9-859a-642f1aaa2a58.png)

### Keywords for `wx:`
![](https://user-images.githubusercontent.com/20026798/59234072-c9daa300-8c1d-11e9-8af8-26d283e909af.png)