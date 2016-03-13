# BBCodeForSponge
Format your chat with BBCode!

Plugin for formatting text with [BBCode](https://en.wikipedia.org/wiki/BBCode). Advantages: BBCode is widely known; most users would use it right away. Disadvantages: not as short as &-formatting code.

###Examples of valid messages:
```
[b]Bold[/b]
[color=red]Red[/color]
[color=green][b][i][u][s]combined[/s][/u][/i][/b][/color]
[url=https://spongepowered.org]Sponge[/url]
[url]https://spongepowered.org[/url]
[quote]Quote[/quote]
[quote=Joshua]Quote from Joshua[/quote]
[spoiler]Spoiler[/spoiler]
[spoiler=Named spoiler]Spoiler[/spoiler]
[pre][b]That's how you bold[/b][/pre]
```

### Plugin compatibility
This plugin has three level of compatibility:

1. Normal compatibility - other plugins are using Appliers, zero problems
2. Legacy compatibility - other plugins are directly substituting whole text with whole text. Plugin will try to manually extract header, zero problems if succeed
3. Emergency compatibility - same as 2, but manual header extraction failed. Text is preserved, but any previous formatting is stripped. Whole line is used as input: this could be undesired if header happens to contain opening BBCode tag.

This plugin works only for chat messages; /me, /say and /msg can't be supported.

###Permissions:
Permission | Description
--- | ---
`bbcodeforsponge.use` | permission for using formatting (general permission)
`bbcodeforsponge.bbcode.b` | permission for using tag [b]
`bbcodeforsponge.bbcode.i` | permission for using tag [i]
`bbcodeforsponge.bbcode.u` | permission for using tag [u]
`bbcodeforsponge.bbcode.s` | permission for using tag [s]
`bbcodeforsponge.bbcode.color` | permission for using tag [color]
`bbcodeforsponge.bbcode.quote` | permission for using tag [quote]
`bbcodeforsponge.bbcode.url` | permission for using tag [url]
`bbcodeforsponge.bbcode.spoiler` | permission for using tag [spoiler]
`bbcodeforsponge.bbcode.pre` | permission for using tag [pre]

Can be used without permission plugin.

###Screenshots:
[![Screenshot](https://dl.dropboxusercontent.com/u/7410519/ScreenS/BBCodeForSponge1_thumb.png)](https://dl.dropboxusercontent.com/u/7410519/ScreenS/BBCodeForSponge1.png) [![Screenshot](https://dl.dropboxusercontent.com/u/7410519/ScreenS/BBCodeForSponge2_thumb.png)](https://dl.dropboxusercontent.com/u/7410519/ScreenS/BBCodeForSponge2.png) [![Screenshot](https://dl.dropboxusercontent.com/u/7410519/ScreenS/BBCodeForSponge3_thumb.png)](https://dl.dropboxusercontent.com/u/7410519/ScreenS/BBCodeForSponge3.png) 

####Compatibility with other plugins (EssentialCmds):
[![Screenshot](https://dl.dropboxusercontent.com/u/7410519/ScreenS/BBCodeForSponge4_thumb.png)](https://dl.dropboxusercontent.com/u/7410519/ScreenS/BBCodeForSponge4.png) 

###Short video:
https://www.youtube.com/watch?v=pk7Fkf_O-IU

###Credits:
Used library kefirBB from kefirFromPerm

###**Download latest stable version**:
[![Latest Stable Version](https://img.shields.io/github/release/VcSaJen/BBCodeForSponge.svg)](https://github.com/VcSaJen/BBCodeForSponge/releases/latest)

####Dev versions (WARNING! Can be even more unstable than releases):
[![Download](https://api.bintray.com/packages/vcsajen/generic/BBCodeForSponge/images/download.svg)](https://bintray.com/vcsajen/generic/BBCodeForSponge)

####Travis status:
[![Build Status](https://travis-ci.org/VcSaJen/BBCodeForSponge.svg?branch=master)](https://travis-ci.org/VcSaJen/BBCodeForSponge)


