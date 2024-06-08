# HashBook

HashBook 是一个 Paper 插件, 通过将书籍数据存储在外部, 实现 Minecraft 中高密度书籍存储.

## :warning: WIP :warning:

**本项目正在进行中, 部分功能还未支持**

## 功能

序列化书籍内容, 生成并保存哈希值, 并按配置将书籍内容保存在外部存储中.

- [x] PDC 存储哈希
- [x] NBT 存储哈希
- [x] 文件存储书籍数据
- [ ] SQLite 存储书籍数据
- [ ] MySQL 存储书籍数据
- [ ] 更方便的指令

## 注意事项

存储后的书籍会删除书页信息, 以便在 Minecraft 中高密度存储书籍, 并防止区块数据溢出.

书籍的哈希值将使用 PDC [(Persistent Data Container)](https://docs.papermc.io/paper/dev/pdc) 或 NBT 存储在书籍中。

哈希值可以是任意字符串. 可以使用外部工具生成带有指定哈希值的 NBT 标签的书籍, 并将书籍数据附加到外部存储中, 实现书籍数据的转移.

外部存储只会保存书籍的书页数据, 不会保存书籍的标题, 作者等信息.

## 优点

- 高效的书籍存储: 通过外部存储减少 Minecraft 区块大小, 防止区块数据溢出.
- 灵活的哈希值管理: 允许使用任意字符串作为哈希值, 方便书籍数据的管理和转移.

## 如何使用

1. 添加 [CommandAPI](https://www.spigotmc.org/resources/api-commandapi-1-16-5-1-20-6.62353/),
   [NBTAPI](https://www.spigotmc.org/resources/nbt-api.7939/)(可选) 前置依赖
2. 安装并配置 HashBook 插件.
