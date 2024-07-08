# File Statistics Utility

## Описание
Утилита для подсчета статистики файлов в заданной директории. Программа поддерживает подсчет количества строк, непустых строк и комментариев в файлах с указанными расширениями. Также она может игнорировать файлы, указанные в `.gitignore`.

## Функциональность
- Подсчет количества файлов, строк, непустых строк и строк комментариев в файлах с заданными расширениями.
- Подсчет общего размера файлов.
- Поддержка нескольких кодировок (UTF-8 и ISO-8859-1).
- Игнорирование файлов, указанных в `.gitignore`.
- Поддержка многопоточности для повышения производительности.
- Фильтрация файлов по расширениям.
- Настройка глубины рекурсивного обхода директорий.

## Требования
- Java 16

## Установка и Запуск
1. Клонируйте репозиторий;
2. Соберите проект с помощью Maven;
3. Запустите JAR проект с нужными аргументами.

### Примеры запуска 
java -jar target/file-statistics-utility-1.0.jar --path <path-to-directory> --max-depth=<depth> --include-ext=<ext1,ext2> --exclude-ext=<ext3,ext4> --output=<format> --thread=<number-of-threads> --git-ignore

java -jar --path "target/file-statistics-utility-1.0.jar" --recursive --max-depth 3 --thread 2 --include-ext=java,txt --exclude-ext=log,tmp --git-ignore --output json

## Параметры командной строки:
-  -p,--path <arg>          Path to directory          : Путь к директории для анализа (обязательный).
-  -d,--max-depth <arg>     Max depth                  : Максимальная глубина обхода.
-  -i,--include-ext <arg>   Include extensions         : Включить файлы с указанными расширениями.
-  -e,--exclude-ext <arg>   Exclude extensions         : Исключить файлы с указанными расширениями.
-  -r,--recursive           Recursive                  : Рекурсивный обход директорий.
-  -o,--output <arg>        Output format              : Формат вывода (например: plain, json, xml).
-  -t,--thread <arg>        Thread count               : Количество потоков для выполнения (минимум 1).
-  -g,--git-ignore          Git ignore                 : Игнорировать файлы, указанные в .gitignore.
-  -h,--help                Print this help message    : Вывод подсказки. 

