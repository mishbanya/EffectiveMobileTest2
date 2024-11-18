Degtyarev M.S.  
@mishbanya  
test project 2 for EffectiveMobile 
14.11.2024  

# ТЗ
## Общая информация

Названия экранов в техническом задании соответствуют названию экранов в Figma.  
Данные для экранов, полученные из запросов на сервер, и данные, продемонстрированные в Figma, могут отличаться.  
Отрисовка элементов из макета в Figma должна быть идентична.  
Все шрифты, цвета и иконки можно брать из Figma.  
На выполнение ТЗ дается 7 дней с момента получения этого файла.

## Используемые источники

- **Ссылка на макеты**: [Figma link](https://www.figma.com/design/wInse3stIxk41PomF5qIou/IT-%D0%BA%D1%83%D1%80%D1%81%D1%8B?node-id=0-1&t=OTk354HeZABqKHGA-1)
- **Ссылка для получения данных (API)**: [Download API](https://github.com/StepicOrg/Stepik-API?tab=readme-ov-file )

## Рекомендуемый стек технологий

**Android**: 
- Kotlin 
- Корутины (если на RX сможете, тоже отлично)
- Flow или LiveData или RX 
- Dagger или Koin
- MVVM
- AdapterDelegates
- Верстка на XML
- Clean Architecture
- Многомодульность (обязательно)
- Room (опционально, если есть время)
- Retrofit (или другая библиотека для работы с сетью)

## 1. Введение
Данное техническое задание предназначено для Junior Android Developers, 
которые будут разрабатывать мобильное приложение для поиска обучающих курсов по программированию на платформе Android. 
Приложение должно помочь пользователям находить подходящие курсы, 
фильтровать их по различным параметрам и отслеживать прогресс обучения.

## 2. Цель разработки
Разработать мобильное приложение для Android, которое позволяет находить обучающие курсы для ИТ-специалистов, 
получать рекомендации, отслеживать прогресс в обучении и получать информацию о новых курсах.

## 3.  Основные требования

## 3.1 Пользовательский интерфейс

### 3.1.1 Меню

Независимо от выбранного экрана в приложении отображается меню. Оно включает фиксированный набор кнопок. В очередности отображения слева направо

1. **Главная** - переход к главному экрану
2. **Избранное** - переход в раздел избранного.
3. **Аккаунт** - переход к экрану с заглушкой.


### 3.1.2 Главный экран

- Экран не имеет заголовка. Для отображения используется JSON (ответ сервера).
- Поле для поиска. В поле отображается хардкодные иконка с лупой и текст “Search courses” в качестве подсказки. Нефункциональный элемент.
- Список курсов с основными данными: название, краткое описание, цена, рейтинг, дата добавления
- Фильтры для поиска: категория (например, Kotlin, Android SDK, UI/UX), уровень сложности (Beginner, Intermediate, Advanced), цена (бесплатный/платный).
- Возможность сортировки курсов по популярности, рейтингу, дате добавления.


### 3.1.3 Экран курса

- Подробная информация о курсе: описание, автор, продолжительность, стоимость, рейтинг, отзывы.
- Кнопка "Перейти на платформу" (например, Udemy, Coursera). При нажатии происходит переход на внешнюю ссылку курса
- Кнопка "Добавить в избранное". Карточка курса добавляется на вкладку Избранное
- «О курсе» статичный текст для всех курсов
- Описание курса берется из платформы, на которой его можно пройти (в данном случае все берется из Stepik’и)
- Кнопка Начать курс - пока заглушка, переводит по ссылке на платформу, где можно пройти курс
- Дата и рейтинг берутся из stepik’и

### 3.1.4 Экран избранного:
- Список курсов, которые пользователь добавил в избранное для дальнейшего просмотра или начала обучения.
- Если курс добавлен в избранное, то флажок окрашивается в зеленый цвет, иначе остается без заливки с белым контуром
- Кнопка Подробнее открывает карточку курса


## 3.2. Функциональные требования


### Поиск курсов:
- Возможность искать курсы по ключевым словам, названию или платформам.
- Фильтрация по категориям, сложности, цене, платформе.
- Показать рекомендации//популярное/новинки в приложении
### Сортировка курсов:
- Курсы можно сортировать по рейтингу, дате добавления, стоимости.
### Просмотр курса:
- Пользователь должен иметь доступ к подробной информации о курсе, включая описание, уровень сложности, цену, отзывы и рейтинг.
- Возможность перехода на сайт платформы для начала обучения или покупки.
### Избранное:
- Пользователи могут добавлять курсы в избранное для удобного доступа в будущем.

## 4. Заключение
Данное техническое задание описывает создание трех экранов мобильного приложения, 
которое будет полезным инструментом для обучающихся ИТ-специалистов, 
помогая им находить подходящие курсы и повышать квалификацию. 
Разработка приложения должна быть выполнена с учетом стандартов разработки для Android, 
обеспечения безопасности данных пользователей и удобства работы с приложением.

