# Wxapp Support
[中文](readme_zh_CN.md)
[![JetBrains plugins](https://img.shields.io/jetbrains/plugin/v/12539-wxapp-support.svg?style=flat-square)](https://plugins.jetbrains.com/plugin/12539-wxapp-support)
[![JetBrains plugins](https://img.shields.io/jetbrains/plugin/d/12539-wxapp-support.svg?style=flat-square)](https://plugins.jetbrains.com/plugin/12539-wxapp-support)
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

## Installation \& Usage
Install IntelliJ IDEA Ultimate, PhpStorm, PyCharm, Rider, RubyMine, WebStorm, AppCode, CLion, GoLand (these ONLY supported!)
open `Settings | Plugins | Browse repositories`,
install `Wxapp Support` plugin.

To download a nightly build (buggy!), visit https://ci.appveyor.com/project/zxj5470/wxapp-intellij/build/artifacts/ .<br/>

## Additional configuration
### Code indentation
You'de better install the `editorconfig` plugin (2018 and later IDEA-U is built-in, but PhpStorm and WebStorm I'm not sure if it's built-in)

Detail see [.editorconfig](https://github.com/zxj5470/wildfire-wxapp/blob/master/.editorconfig)

### Wxapp Code completion hint

All right, that's all right!
![](https://user-images.githubusercontent.com/20026798/59198181-ac79ea80-8bc5-11e9-9512-3240faeaefaf.png)

## Relevant content

### Compatibility
Because of the navigation for CSS, the minimum compatible version is 172.

### Plugin Features and related solutions
- js
	- Embedded wx.d.ts TypeDefinition file
	- Navigation: above `All right, that's all right!` in that picture, ctrl/command + left-click on the string content of url to jump to .js. 
      - even if you write `/ pages/index/ index`, you support jump. Of course, it is not recommended that you write this way.
	- Embedded [Matchmaker](https://github.com/lypeer/Matchmaker) plugin functions.
		- Alt + Insert or right-click `Generate... | Make match`
	- and modify to insert the corresponding function at the current cursor position. 
	That plug-in is inserted directly after the `Page({`), this is Kichiku.	
	- for yellow of semicolons:
	![](https://user-images.githubusercontent.com/20026798/59234137-fd1d3200-8c1d-11e9-93dc-682a69237cdc.png)
- wxml Navigation
	- `{{ identifier }}` ctrl/command + left-click => Jump to variable
	- `bind***="functionName"` Jump to the corresponding function
	- `class='cls'` Jump to `${currentPage}.wxss` 或 `app.wxss`
	- Turn Off empty tag warning by Alt+Enter：
	![](https://user-images.githubusercontent.com/20026798/59233969-5042b500-8c1d-11e9-9b54-08a4cfee8fd8.png)
		
- wxss
	- rpx：
		![](https://user-images.githubusercontent.com/20026798/59233255-4a97a000-8c1a-11e9-819f-e648f7ea1ef0.png)
		- Alt/Option + Enter，Turn Off inspection。
		![](https://user-images.githubusercontent.com/20026798/59233270-58e5bc00-8c1a-11e9-9d81-736709fe2633.png)
	

## Pending……
- wxml idnent
- wxss class jump to wxml

## ScreenShot

### WXML
![](https://user-images.githubusercontent.com/20026798/59234019-82541700-8c1d-11e9-859a-642f1aaa2a58.png)

### Keywords for `wx:`
![](https://user-images.githubusercontent.com/20026798/59234072-c9daa300-8c1d-11e9-8af8-26d283e909af.png)

### WXSS
![](https://user-images.githubusercontent.com/20026798/59233421-f214d280-8c1a-11e9-843f-57a498e8e248.png)
