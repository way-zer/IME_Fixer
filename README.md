# IME Fixer
A mod in order to resolve IME problem in windows
一个尝试解决Windows下中文输入法问题的MOD
# Features
- [x] fix show of candidate list 修复候选框不显示
- [x] fix show of composition list 修复拼写框不显示
- [x] fix position of the candidate list 修复输入法显示位置(聊天/指令)
- [x] support all input field 支持信息版等各种输入框
- [x] auto enable/disable input method 无需输入时自动关闭输入法
- [x] fix `backspace` when composite 修复拼写时删除问题
## Quick Start
Install the jar into `mods` (Only support win64)
安装jar到mods(目前仅支持WIN64系统)
## 开发
开发环境 IDEA JAVA MINGW
相关技术 JNI WindowsAPI(imm32 msctf)
环境配置 导入仓库到IDEA中,使用IDEA进行编译,打包