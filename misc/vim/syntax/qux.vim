" Vim syntax file
" Language:         Qux
" Maintainer:       Henry J. Wylde
" URL:              https://github.com/hjwylde/qux/
" Latest Revision:  2014-07-18
" Filenames:        *.qux
" Version:          0.2.3

if exists("b:current_syntax")
    finish
endif

" Options

let foldmethod='indent'

" Keywords

syn keyword bool false true
syn keyword externals import package
syn keyword keywords const is type
syn keyword operators and iff implies in nin or xor
syn keyword statements elif else for if print return while
syn keyword types any bool int list null obj real record set str void
syn keyword todo contained TODO FIXME

" Matches

syn match escapeSequence contained '\\[fnrt'\\]'
syn match escapeSequence contained '\\u\x\{4\}'

syn match escapeError contained '\\.'

syn match int '\d\+'
syn match int '-\d\+'
syn match int '0b[01]\+'
syn match int '0o[0-7]\+'
syn match int '0x\x\+'

syn match real '\d\+\.\d\+'
syn match real '-\d\+\.\d\+'
syn match real '\d\+\.\d\+e[+-]\d\+'
syn match real '-\d\+\.\d\+e[+-]\d\+'
syn match real '\d\+e[+-]\d\+'
syn match real '-\d\+e[+-]\d\+'

syn match delimiters '[(){}[].,;:$]'

"syn match operators '='
"syn match operators '!='
"syn match operators '<'
"syn match operators '<='
"syn match operators '>'
"syn match operators '>='
"syn match operators '\.\.'
"syn match operators '\*'
"syn match operators '+'
"syn match operators '-'
"syn match operators '/'
"syn match operators '%'

syn match tab '\t\+'

syn match types '[A-Z]'
syn match types '[A-Z][a-zA-Z0-9_]\+'

syn match constants '[A-Z][A-Z0-9_]\+'

syn match identifiers '[a-z_]'
syn match identifiers '[a-z_][a-zA-Z0-9_]\+'

" Regions

syn region str          start="'" end="'" keepend contains=escapeSequence,escapeError

syn region commentLine  start='#' end='$' keepend contains=todo,@Spell

syntax include @Html syntax/html.vim

syn region commentDoc   start='/\*\*' end='\*/' keepend contains=todo,@Html,@Spell

syn region commentBlock start='/\*' end='\*/' keepend contains=todo,@Spell

" Highlights

hi def link bool            Constant
hi def link commentBlock    Comment
hi def link commentDoc      Comment
hi def link commentLine     Comment
hi def link constants       Constant
hi def link delimiters      Special
hi def link escapeError     Error
hi def link escapeSequence  Special
hi def link externals       PreProc
hi def link identifiers     Normal
hi def link int             Constant
hi def link keywords        Statement
hi def link operators       Statement
hi def link real            Constant
hi def link statements      Statement
hi def link str             Constant
hi def link tab             Error
hi def link todo            Todo
hi def link types           Type

let b:current_syntax = 'qux'

