package com.example.music_land.data;

import com.example.music_land.data.model.QuizQuestion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class QuizData {
    private static final HashMap<String, List<QuizQuestion>> genreQuestions = new HashMap<>();
    private static final int QUESTIONS_PER_QUIZ = 10;
    private static final Random random = new Random();

    static {
        // Rock questions
        List<QuizQuestion> rockQuestions = new ArrayList<>();
        rockQuestions.add(new QuizQuestion(
            "Какая группа известна как 'Великолепная четверка'?",
            new String[]{"The Rolling Stones", "The Beatles", "Led Zeppelin", "Pink Floyd"},
            1, // The Beatles
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Кого называют 'Королем рок-н-ролла'?",
            new String[]{"Чак Берри", "Элвис Пресли", "Литл Ричард", "Бадди Холли"},
            1, // Элвис Пресли
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Какая группа записала альбом 'The Dark Side of the Moon'?",
            new String[]{"Pink Floyd", "Led Zeppelin", "The Beatles", "The Rolling Stones"},
            0, // Pink Floyd
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Кто был вокалистом группы Queen?",
            new String[]{"Фредди Меркьюри", "Джон Леннон", "Мик Джаггер", "Дэвид Боуи"},
            0, // Фредди Меркьюри
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Какая группа записала песню 'Stairway to Heaven'?",
            new String[]{"Led Zeppelin", "The Beatles", "Pink Floyd", "The Rolling Stones"},
            0, // Led Zeppelin
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "В каком году образовалась группа The Beatles?",
            new String[]{"1957", "1960", "1962", "1964"},
            1, // 1960
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Какой альбом The Rolling Stones считается одним из лучших в истории рока?",
            new String[]{"Exile on Main St.", "Let It Bleed", "Sticky Fingers", "Beggars Banquet"},
            0, // Exile on Main St.
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Кто был гитаристом группы Pink Floyd?",
            new String[]{"Дэвид Гилмор", "Джимми Пейдж", "Эрик Клэптон", "Джордж Харрисон"},
            0, // Дэвид Гилмор
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Какой альбом The Beatles стал последним?",
            new String[]{"Abbey Road", "Let It Be", "The White Album", "Sgt. Pepper's"},
            0, // Abbey Road
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Кто был барабанщиком группы Led Zeppelin?",
            new String[]{"Джон Бонэм", "Ринго Старр", "Чарли Уоттс", "Кит Мун"},
            0, // Джон Бонэм
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Какая группа записала песню 'Bohemian Rhapsody'?",
            new String[]{"Queen", "The Beatles", "Led Zeppelin", "Pink Floyd"},
            0, // Queen
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Кто был вокалистом группы The Doors?",
            new String[]{"Джим Моррисон", "Джон Леннон", "Мик Джаггер", "Фредди Меркьюри"},
            0, // Джим Моррисон
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Какой альбом Nirvana стал самым продаваемым?",
            new String[]{"Nevermind", "In Utero", "Bleach", "MTV Unplugged"},
            0, // Nevermind
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Кто был вокалистом группы AC/DC?",
            new String[]{"Бон Скотт", "Брайан Джонсон", "Роберт Плант", "Оззи Осборн"},
            0, // Бон Скотт
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Какая группа записала песню 'Sweet Child O' Mine'?",
            new String[]{"Guns N' Roses", "Metallica", "Iron Maiden", "Judas Priest"},
            0, // Guns N' Roses
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Кто был вокалистом группы The Who?",
            new String[]{"Роджер Долтри", "Роберт Плант", "Мик Джаггер", "Джон Леннон"},
            0, // Роджер Долтри
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Какой альбом The Rolling Stones записан в 1969 году?",
            new String[]{"Let It Bleed", "Beggars Banquet", "Sticky Fingers", "Exile on Main St."},
            0, // Let It Bleed
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Кто был гитаристом группы The Beatles?",
            new String[]{"Джордж Харрисон", "Джон Леннон", "Пол Маккартни", "Ринго Старр"},
            0, // Джордж Харрисон
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Какая группа записала песню 'Smoke on the Water'?",
            new String[]{"Deep Purple", "Led Zeppelin", "Black Sabbath", "Pink Floyd"},
            0, // Deep Purple
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Кто был вокалистом группы Black Sabbath до Оззи Осборна?",
            new String[]{"Тони Мартин", "Ронни Джеймс Дио", "Ян Гиллан", "Дэвид Ковердейл"},
            0, // Тони Мартин
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Какой альбом Pink Floyd записан в 1977 году?",
            new String[]{"Animals", "The Wall", "Wish You Were Here", "The Dark Side of the Moon"},
            0, // Animals
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Кто был барабанщиком группы The Beatles?",
            new String[]{"Ринго Старр", "Джон Бонэм", "Чарли Уоттс", "Кит Мун"},
            0, // Ринго Старр
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Какая группа записала песню 'Hotel California'?",
            new String[]{"Eagles", "The Rolling Stones", "The Beatles", "Led Zeppelin"},
            0, // Eagles
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Кто был вокалистом группы The Rolling Stones?",
            new String[]{"Мик Джаггер", "Кит Ричардс", "Ронни Вуд", "Билл Уаймен"},
            0, // Мик Джаггер
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Какой альбом Led Zeppelin записан в 1971 году?",
            new String[]{"Led Zeppelin IV", "Houses of the Holy", "Physical Graffiti", "Presence"},
            0, // Led Zeppelin IV
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Кто был гитаристом группы The Rolling Stones?",
            new String[]{"Кит Ричардс", "Мик Джаггер", "Ронни Вуд", "Билл Уаймен"},
            0, // Кит Ричардс
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Какая группа записала песню 'Another Brick in the Wall'?",
            new String[]{"Pink Floyd", "Led Zeppelin", "The Beatles", "The Rolling Stones"},
            0, // Pink Floyd
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Кто был вокалистом группы Deep Purple в 1970-х?",
            new String[]{"Ян Гиллан", "Дэвид Ковердейл", "Род Эванс", "Гленн Хьюз"},
            0, // Ян Гиллан
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Какой альбом The Who записан в 1971 году?",
            new String[]{"Who's Next", "Tommy", "Quadrophenia", "The Who Sell Out"},
            0, // Who's Next
            10
        ));
        rockQuestions.add(new QuizQuestion(
            "Кто был барабанщиком группы The Rolling Stones?",
            new String[]{"Чарли Уоттс", "Кит Мун", "Джон Бонэм", "Ринго Старр"},
            0, // Чарли Уоттс
            10
        ));
        genreQuestions.put("Rock", rockQuestions);

        // Pop questions
        List<QuizQuestion> popQuestions = new ArrayList<>();
        popQuestions.add(new QuizQuestion(
            "Кого называют 'Королевой поп-музыки'?",
            new String[]{"Бейонсе", "Мадонна", "Леди Гага", "Бритни Спирс"},
            1, // Мадонна
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Какой альбом Майкла Джексона стал самым продаваемым?",
            new String[]{"Thriller", "Bad", "Dangerous", "Off the Wall"},
            0, // Thriller
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Какая песня Майкла Джексона стала первой в истории, получившей 7 номинаций на Грэмми?",
            new String[]{"Billie Jean", "Thriller", "Beat It", "Smooth Criminal"},
            0, // Billie Jean
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Какой альбом Мадонны стал самым продаваемым?",
            new String[]{"Like a Virgin", "True Blue", "Ray of Light", "Like a Prayer"},
            1, // True Blue
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Кто записал песню 'Like a Prayer'?",
            new String[]{"Мадонна", "Бритни Спирс", "Леди Гага", "Бейонсе"},
            0, // Мадонна
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Какой альбом Бритни Спирс стал самым продаваемым?",
            new String[]{"...Baby One More Time", "Oops!... I Did It Again", "Britney", "In the Zone"},
            0, // ...Baby One More Time
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Кто записал песню 'Poker Face'?",
            new String[]{"Леди Гага", "Кэти Перри", "Рианна", "Бейонсе"},
            0, // Леди Гага
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Какой альбом Бейонсе получил Грэмми за 'Альбом года'?",
            new String[]{"Lemonade", "Beyoncé", "I Am... Sasha Fierce", "4"},
            0, // Lemonade
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Кто записал песню 'Umbrella'?",
            new String[]{"Рианна", "Бейонсе", "Леди Гага", "Кэти Перри"},
            0, // Рианна
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Какой альбом Кэти Перри стал самым продаваемым?",
            new String[]{"Teenage Dream", "One of the Boys", "Prism", "Witness"},
            0, // Teenage Dream
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Кто записал песню 'Bad Romance'?",
            new String[]{"Леди Гага", "Кэти Перри", "Рианна", "Бейонсе"},
            0, // Леди Гага
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Какой альбом Тейлор Свифт стал самым продаваемым?",
            new String[]{"1989", "Red", "Fearless", "Reputation"},
            0, // 1989
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Кто записал песню 'Shape of You'?",
            new String[]{"Эд Ширан", "Джастин Бибер", "Шон Мендес", "Бруно Марс"},
            0, // Эд Ширан
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Какой альбом Адель стал самым продаваемым?",
            new String[]{"21", "19", "25", "30"},
            0, // 21
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Кто записал песню 'Rolling in the Deep'?",
            new String[]{"Адель", "Дуа Липа", "Элли Голдинг", "Джесси Джей"},
            0, // Адель
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Какой альбом Джастина Бибера стал самым продаваемым?",
            new String[]{"Purpose", "Believe", "My World 2.0", "Changes"},
            0, // Purpose
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Кто записал песню 'Sorry'?",
            new String[]{"Джастин Бибер", "Эд Ширан", "Шон Мендес", "Бруно Марс"},
            0, // Джастин Бибер
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Какой альбом Бруно Марса стал самым продаваемым?",
            new String[]{"24K Magic", "Doo-Wops & Hooligans", "Unorthodox Jukebox", "An Evening with Silk Sonic"},
            0, // 24K Magic
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Кто записал песню 'Uptown Funk'?",
            new String[]{"Марк Ронсон и Бруно Марс", "Джастин Тимберлейк", "Фаррелл Уильямс", "Питбуль"},
            0, // Марк Ронсон и Бруно Марс
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Какой альбом Дуа Липы стал самым продаваемым?",
            new String[]{"Future Nostalgia", "Dua Lipa", "Club Future Nostalgia", "Dance the Night"},
            0, // Future Nostalgia
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Кто записал песню 'Don't Start Now'?",
            new String[]{"Дуа Липа", "Джесси Джей", "Элли Голдинг", "Рита Ора"},
            0, // Дуа Липа
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Какой альбом Шона Мендеса стал самым продаваемым?",
            new String[]{"Wonder", "Handwritten", "Illuminate", "Shawn Mendes"},
            0, // Wonder
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Кто записал песню 'Stitches'?",
            new String[]{"Шон Мендес", "Эд Ширан", "Джастин Бибер", "Бруно Марс"},
            0, // Шон Мендес
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Какой альбом Арианы Гранде стал самым продаваемым?",
            new String[]{"Thank U, Next", "Sweetener", "Positions", "Dangerous Woman"},
            0, // Thank U, Next
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Кто записал песню '7 Rings'?",
            new String[]{"Ариана Гранде", "Дуа Липа", "Билл Айлиш", "Холзи"},
            0, // Ариана Гранде
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Какой альбом Билл Айлиш стал самым продаваемым?",
            new String[]{"When We All Fall Asleep, Where Do We Go?", "Happier Than Ever", "Don't Smile at Me", "Guitar Songs"},
            0, // When We All Fall Asleep, Where Do We Go?
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Кто записал песню 'Bad Guy'?",
            new String[]{"Билл Айлиш", "Ариана Гранде", "Дуа Липа", "Холзи"},
            0, // Билл Айлиш
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Какой альбом Холзи стал самым продаваемым?",
            new String[]{"Manic", "Badlands", "Hopeless Fountain Kingdom", "If I Can't Have Love, I Want Power"},
            0, // Manic
            10
        ));
        popQuestions.add(new QuizQuestion(
            "Кто записал песню 'Without Me'?",
            new String[]{"Холзи", "Билл Айлиш", "Ариана Гранде", "Дуа Липа"},
            0, // Холзи
            10
        ));
        genreQuestions.put("Pop", popQuestions);

        // Hip-Hop questions
        List<QuizQuestion> hipHopQuestions = new ArrayList<>();
        hipHopQuestions.add(new QuizQuestion(
            "В каком году зародился хип-хоп?",
            new String[]{"1965", "1973", "1980", "1990"},
            1, // 1973
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Кто считается «отцом хип-хопа»?",
            new String[]{"Grandmaster Flash", "Afrika Bambaataa", "DJ Kool Herc", "Russell Simmons"},
            2, // DJ Kool Herc
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Какая дата считается днем рождения хип-хопа?",
            new String[]{"11 июля 1973", "11 августа 1973", "15 сентября 1973", "11 октября 1973"},
            1, // 11 августа 1973
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Какое настоящее имя у Eminem?",
            new String[]{"Маршалл Брюс Мэтерс III", "Дуэйн Майкл Картер", "Кёртис Джеймс Джексон III", "Кендрик Ламар Дакворт"},
            0, // Маршалл Брюс Мэтерс III
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "В каком городе вырос Eminem?",
            new String[]{"Нью-Йорк", "Лос-Анджелес", "Чикаго", "Детройт"},
            3, // Детройт
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Какой альбом Eminem считается его коммерческим прорывом?",
            new String[]{"Infinite", "The Slim Shady LP", "The Marshall Mathers LP", "Recovery"},
            1, // The Slim Shady LP
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "За какую песню Eminem получил Оскар?",
            new String[]{"Lose Yourself", "Stan", "The Real Slim Shady", "Without Me"},
            0, // Lose Yourself
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Кто открыл талант Eminem и подписал его на свой лейбл?",
            new String[]{"Jay-Z", "Dr. Dre", "Kanye West", "Rick Rubin"},
            1, // Dr. Dre
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Как называется альтер-эго Eminem?",
            new String[]{"Marshall", "B-Rabbit", "Slim Shady", "M&M"},
            2, // Slim Shady
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Из какого фильма с участием Eminem песня «Lose Yourself»?",
            new String[]{"Get Rich or Die Tryin'", "8 Mile", "Notorious", "Straight Outta Compton"},
            1, // 8 Mile
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Какое полное имя при рождении у 2Pac?",
            new String[]{"Тупак Амару Шакур", "Лесэйн Пэриш Крукс", "Кёртис Джексон", "Кристофер Уоллес"},
            1, // Лесэйн Пэриш Крукс
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Кем были родители 2Pac?",
            new String[]{"Музыкантами", "Активистами «Черных пантер»", "Учителями", "Адвокатами"},
            1, // Активистами «Черных пантер»
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "В каком учебном заведении учился 2Pac в Балтиморе?",
            new String[]{"Baltimore College", "Baltimore School for the Arts", "Baltimore High School", "Baltimore Academy of Music"},
            1, // Baltimore School for the Arts
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "С какой группой 2Pac начинал свою карьеру?",
            new String[]{"N.W.A", "Digital Underground", "Public Enemy", "Wu-Tang Clan"},
            1, // Digital Underground
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Какой альбом 2Pac был записан, когда он находился в тюрьме?",
            new String[]{"2Pacalypse Now", "Me Against the World", "All Eyez on Me", "The Don Killuminati"},
            1, // Me Against the World
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Какой псевдоним 2Pac использовал для своего последнего прижизненного альбома?",
            new String[]{"MC New York", "Makaveli", "Thug Life", "Outlaw"},
            1, // Makaveli
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Когда произошло покушение на 2Pac в Лас-Вегасе?",
            new String[]{"7 сентября 1996", "13 сентября 1996", "7 марта 1997", "9 марта 1997"},
            0, // 7 сентября 1996
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Какой альбом 2Pac считается одним из самых продаваемых рэп-альбомов всех времен?",
            new String[]{"All Eyez on Me", "Me Against the World", "The Don Killuminati", "Strictly 4 My N.I.G.G.A.Z."},
            0, // All Eyez on Me
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Какой хит 2Pac был записан с Dr. Dre?",
            new String[]{"Dear Mama", "California Love", "Hit 'Em Up", "Changes"},
            1, // California Love
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Какой фильм считается одним из первых в карьере 2Pac как актера?",
            new String[]{"Juice", "Poetic Justice", "Above the Rim", "Gridlock'd"},
            0, // Juice
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Сколько песен, по некоторым данным, записал 2Pac за 11 месяцев на Death Row Records?",
            new String[]{"Около 50", "Около 100", "Более 200", "Более 500"},
            2, // Более 200
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Какие четыре элемента составляют культуру хип-хопа?",
            new String[]{"Рэп, Диджеинг, Брейк-данс, Граффити", "Музыка, Танец, Одежда, Сленг", "Битбоксинг, Фристайл, Битмейкинг, MCing", "Скретчинг, Семплинг, Фристайл, Битмейкинг"},
            0, // Рэп, Диджеинг, Брейк-данс, Граффити
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Что означает аббревиатура MC в хип-хопе?",
            new String[]{"Music Creator", "Microphone Controller", "Master of Creativity", "Master of Ceremonies"},
            3, // Master of Ceremonies
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Какую технику изобрел DJ Kool Herc, ставшую основой хип-хопа?",
            new String[]{"Скретчинг", "Семплинг", "Merry-go-round (удлинение брейков)", "Фристайл"},
            2, // Merry-go-round (удлинение брейков)
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "В каком году Eminem был введен в Зал славы рок-н-ролла?",
            new String[]{"2018", "2020", "2022", "Пока не введен"},
            2, // 2022
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Какая песня Eminem демонстрирует его рекордную скорость чтения рэпа?",
            new String[]{"Lose Yourself", "Rap God", "Godzilla", "Without Me"},
            1, // Rap God
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "В каком году 2Pac был введен в Зал славы рок-н-ролла?",
            new String[]{"2005", "2010", "2017", "Пока не введен"},
            2, // 2017
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Название какого лейбла записи связано с конфликтом Восточного и Западного побережья?",
            new String[]{"Def Jam", "Roc-A-Fella", "Death Row Records", "Aftermath Entertainment"},
            2, // Death Row Records
            10
        ));
        hipHopQuestions.add(new QuizQuestion(
            "Какой трек считается первым коммерческим хип-хоп хитом?",
            new String[]{"The Message", "Planet Rock", "Rapper's Delight", "Walk This Way"},
            2, // Rapper's Delight
            10
        ));
        genreQuestions.put("Hip Hop", hipHopQuestions);

        // Jazz questions
        List<QuizQuestion> jazzQuestions = new ArrayList<>();
        jazzQuestions.add(new QuizQuestion(
            "Кого называют 'Отцом джаза'?",
            new String[]{"Луи Армстронг", "Дюк Эллингтон", "Чарли Паркер", "Майлз Дэвис"},
            0, // Луи Армстронг
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Какой город считается родиной джаза?",
            new String[]{"Чикаго", "Нью-Йорк", "Новый Орлеан", "Лос-Анджелес"},
            2, // Новый Орлеан
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Кто записал альбом 'Kind of Blue'?",
            new String[]{"Майлз Дэвис", "Джон Колтрейн", "Чарли Паркер", "Луи Армстронг"},
            0, // Майлз Дэвис
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Какой инструмент был основным у Луи Армстронга?",
            new String[]{"Труба", "Саксофон", "Фортепиано", "Контрабас"},
            0, // Труба
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Кто записал альбом 'A Love Supreme'?",
            new String[]{"Джон Колтрейн", "Майлз Дэвис", "Чарли Паркер", "Луи Армстронг"},
            0, // Джон Колтрейн
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Какой стиль джаза зародился в 1940-х годах?",
            new String[]{"Бибоп", "Свинг", "Кул-джаз", "Фри-джаз"},
            0, // Бибоп
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Кто записал альбом 'Time Out'?",
            new String[]{"Дейв Брубек", "Майлз Дэвис", "Джон Колтрейн", "Чарли Паркер"},
            0, // Дейв Брубек
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Какой инструмент был основным у Чарли Паркера?",
            new String[]{"Альт-саксофон", "Труба", "Фортепиано", "Контрабас"},
            0, // Альт-саксофон
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Кто записал альбом 'Ellington at Newport'?",
            new String[]{"Дюк Эллингтон", "Майлз Дэвис", "Джон Колтрейн", "Луи Армстронг"},
            0, // Дюк Эллингтон
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Какой стиль джаза зародился в 1960-х годах?",
            new String[]{"Фри-джаз", "Бибоп", "Свинг", "Кул-джаз"},
            0, // Фри-джаз
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Кто записал альбом 'The Shape of Jazz to Come'?",
            new String[]{"Орнетт Коулман", "Майлз Дэвис", "Джон Колтрейн", "Чарли Паркер"},
            0, // Орнетт Коулман
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Какой инструмент был основным у Джона Колтрейна?",
            new String[]{"Тенор-саксофон", "Труба", "Фортепиано", "Контрабас"},
            0, // Тенор-саксофон
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Кто записал альбом 'Giant Steps'?",
            new String[]{"Джон Колтрейн", "Майлз Дэвис", "Чарли Паркер", "Луи Армстронг"},
            0, // Джон Колтрейн
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Какой стиль джаза зародился в 1950-х годах?",
            new String[]{"Кул-джаз", "Бибоп", "Свинг", "Фри-джаз"},
            0, // Кул-джаз
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Кто записал альбом 'Birth of the Cool'?",
            new String[]{"Майлз Дэвис", "Джон Колтрейн", "Чарли Паркер", "Луи Армстронг"},
            0, // Майлз Дэвис
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Какой инструмент был основным у Майлза Дэвиса?",
            new String[]{"Труба", "Саксофон", "Фортепиано", "Контрабас"},
            0, // Труба
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Кто записал альбом 'Bitches Brew'?",
            new String[]{"Майлз Дэвис", "Джон Колтрейн", "Чарли Паркер", "Луи Армстронг"},
            0, // Майлз Дэвис
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Какой стиль джаза зародился в 1970-х годах?",
            new String[]{"Джаз-фьюжн", "Бибоп", "Свинг", "Фри-джаз"},
            0, // Джаз-фьюжн
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Кто записал альбом 'Head Hunters'?",
            new String[]{"Херби Хэнкок", "Майлз Дэвис", "Джон Колтрейн", "Чарли Паркер"},
            0, // Херби Хэнкок
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Какой инструмент был основным у Херби Хэнкока?",
            new String[]{"Фортепиано", "Труба", "Саксофон", "Контрабас"},
            0, // Фортепиано
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Кто записал альбом 'The Köln Concert'?",
            new String[]{"Кит Джарретт", "Херби Хэнкок", "Майлз Дэвис", "Джон Колтрейн"},
            0, // Кит Джарретт
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Какой стиль джаза зародился в 1980-х годах?",
            new String[]{"Смус-джаз", "Бибоп", "Свинг", "Фри-джаз"},
            0, // Смус-джаз
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Кто записал альбом 'Tutu'?",
            new String[]{"Майлз Дэвис", "Херби Хэнкок", "Кит Джарретт", "Джон Колтрейн"},
            0, // Майлз Дэвис
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Какой инструмент был основным у Уинтона Марсалиса?",
            new String[]{"Труба", "Саксофон", "Фортепиано", "Контрабас"},
            0, // Труба
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Кто записал альбом 'Black Codes (From the Underground)'?",
            new String[]{"Уинтон Марсалис", "Майлз Дэвис", "Херби Хэнкок", "Кит Джарретт"},
            0, // Уинтон Марсалис
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Какой стиль джаза зародился в 1990-х годах?",
            new String[]{"Академический джаз", "Бибоп", "Свинг", "Фри-джаз"},
            0, // Академический джаз
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Кто записал альбом 'The New Standard'?",
            new String[]{"Херби Хэнкок", "Уинтон Марсалис", "Майлз Дэвис", "Кит Джарретт"},
            0, // Херби Хэнкок
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Какой инструмент был основным у Пэта Мэтини?",
            new String[]{"Гитара", "Труба", "Саксофон", "Фортепиано"},
            0, // Гитара
            10
        ));
        jazzQuestions.add(new QuizQuestion(
            "Кто записал альбом 'Bright and Beautiful'?",
            new String[]{"Пэт Мэтини", "Херби Хэнкок", "Уинтон Марсалис", "Кит Джарретт"},
            0, // Пэт Мэтини
            10
        ));
        genreQuestions.put("Jazz", jazzQuestions);

        // Classical questions
        List<QuizQuestion> classicalQuestions = new ArrayList<>();
        classicalQuestions.add(new QuizQuestion(
            "Кто написал 'Симфонию №9'?",
            new String[]{"Моцарт", "Бетховен", "Бах", "Чайковский"},
            1, // Бетховен
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Какой период пришел после Барокко?",
            new String[]{"Ренессанс", "Классицизм", "Романтизм", "Модерн"},
            1, // Классицизм
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Кто написал 'Реквием'?",
            new String[]{"Моцарт", "Бетховен", "Бах", "Чайковский"},
            0, // Моцарт
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Какой композитор написал 'Времена года'?",
            new String[]{"Вивальди", "Бах", "Моцарт", "Бетховен"},
            0, // Вивальди
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Кто написал 'Лунную сонату'?",
            new String[]{"Бетховен", "Моцарт", "Бах", "Чайковский"},
            0, // Бетховен
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Какой композитор написал 'Токкату и фугу ре минор'?",
            new String[]{"Бах", "Моцарт", "Бетховен", "Чайковский"},
            0, // Бах
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Кто написал 'Лебединое озеро'?",
            new String[]{"Чайковский", "Моцарт", "Бетховен", "Бах"},
            0, // Чайковский
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Какой композитор написал 'Кармен'?",
            new String[]{"Бизе", "Моцарт", "Бетховен", "Чайковский"},
            0, // Бизе
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Кто написал 'Вальс цветов'?",
            new String[]{"Чайковский", "Моцарт", "Бетховен", "Бах"},
            0, // Чайковский
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Какой композитор написал 'Весну священную'?",
            new String[]{"Стравинский", "Моцарт", "Бетховен", "Чайковский"},
            0, // Стравинский
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Кто написал 'Симфонию №40'?",
            new String[]{"Моцарт", "Бетховен", "Бах", "Чайковский"},
            0, // Моцарт
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Какой композитор написал 'Аве Мария'?",
            new String[]{"Шуберт", "Бах", "Моцарт", "Бетховен"},
            0, // Шуберт
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Кто написал 'Симфонию №5'?",
            new String[]{"Бетховен", "Моцарт", "Бах", "Чайковский"},
            0, // Бетховен
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Какой композитор написал 'Весеннюю песню'?",
            new String[]{"Мендельсон", "Бах", "Моцарт", "Бетховен"},
            0, // Мендельсон
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Кто написал 'Симфонию №6'?",
            new String[]{"Чайковский", "Моцарт", "Бетховен", "Бах"},
            0, // Чайковский
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Какой композитор написал 'Турецкий марш'?",
            new String[]{"Моцарт", "Бетховен", "Бах", "Чайковский"},
            0, // Моцарт
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Кто написал 'Симфонию №3'?",
            new String[]{"Бетховен", "Моцарт", "Бах", "Чайковский"},
            0, // Бетховен
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Какой композитор написал 'Вальс-фантазию'?",
            new String[]{"Глинка", "Бах", "Моцарт", "Бетховен"},
            0, // Глинка
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Кто написал 'Симфонию №7'?",
            new String[]{"Бетховен", "Моцарт", "Бах", "Чайковский"},
            0, // Бетховен
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Какой композитор написал 'Полонез'?",
            new String[]{"Шопен", "Бах", "Моцарт", "Бетховен"},
            0, // Шопен
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Кто написал 'Симфонию №8'?",
            new String[]{"Бетховен", "Моцарт", "Бах", "Чайковский"},
            0, // Бетховен
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Какой композитор написал 'Венгерские танцы'?",
            new String[]{"Брамс", "Бах", "Моцарт", "Бетховен"},
            0, // Брамс
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Кто написал 'Симфонию №4'?",
            new String[]{"Чайковский", "Моцарт", "Бетховен", "Бах"},
            0, // Чайковский
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Какой композитор написал 'Симфонические танцы'?",
            new String[]{"Рахманинов", "Бах", "Моцарт", "Бетховен"},
            0, // Рахманинов
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Кто написал 'Симфонию №2'?",
            new String[]{"Бетховен", "Моцарт", "Бах", "Чайковский"},
            0, // Бетховен
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Какой композитор написал 'Симфонические вариации'?",
            new String[]{"Франк", "Бах", "Моцарт", "Бетховен"},
            0, // Франк
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Кто написал 'Симфонию №1'?",
            new String[]{"Бетховен", "Моцарт", "Бах", "Чайковский"},
            0, // Бетховен
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Какой композитор написал 'Симфоническую поэму'?",
            new String[]{"Лист", "Бах", "Моцарт", "Бетховен"},
            0, // Лист
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Кто написал 'Симфонию №41'?",
            new String[]{"Моцарт", "Бетховен", "Бах", "Чайковский"},
            0, // Моцарт
            10
        ));
        classicalQuestions.add(new QuizQuestion(
            "Какой композитор написал 'Симфоническую сюиту'?",
            new String[]{"Римский-Корсаков", "Бах", "Моцарт", "Бетховен"},
            0, // Римский-Корсаков
            10
        ));
        genreQuestions.put("Classical", classicalQuestions);

        // Metal questions
        List<QuizQuestion> metalQuestions = new ArrayList<>();
        metalQuestions.add(new QuizQuestion(
            "Какая группа считается пионерами хеви-метала?",
            new String[]{"Metallica", "Black Sabbath", "Iron Maiden", "Judas Priest"},
            1, // Black Sabbath
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Какой поджанр метала известен быстрым темпом и агрессивным звучанием?",
            new String[]{"Дум-метал", "Трэш-метал", "Пауэр-метал", "Прогрессив-метал"},
            1, // Трэш-метал
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Кто был вокалистом группы Metallica?",
            new String[]{"Джеймс Хэтфилд", "Оззи Осборн", "Брюс Дикинсон", "Роб Хэлфорд"},
            0, // Джеймс Хэтфилд
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Какой альбом Metallica стал самым продаваемым?",
            new String[]{"Metallica (Black Album)", "Master of Puppets", "Ride the Lightning", "And Justice for All"},
            0, // Metallica (Black Album)
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Кто был вокалистом группы Black Sabbath?",
            new String[]{"Оззи Осборн", "Джеймс Хэтфилд", "Брюс Дикинсон", "Роб Хэлфорд"},
            0, // Оззи Осборн
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Какой альбом Iron Maiden стал самым продаваемым?",
            new String[]{"The Number of the Beast", "Powerslave", "Seventh Son of a Seventh Son", "Fear of the Dark"},
            0, // The Number of the Beast
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Кто был вокалистом группы Judas Priest?",
            new String[]{"Роб Хэлфорд", "Оззи Осборн", "Джеймс Хэтфилд", "Брюс Дикинсон"},
            0, // Роб Хэлфорд
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Какой альбом Slayer стал самым влиятельным в трэш-метале?",
            new String[]{"Reign in Blood", "South of Heaven", "Seasons in the Abyss", "Hell Awaits"},
            0, // Reign in Blood
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Кто был вокалистом группы Megadeth?",
            new String[]{"Дэйв Мастейн", "Джеймс Хэтфилд", "Оззи Осборн", "Брюс Дикинсон"},
            0, // Дэйв Мастейн
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Какой альбом Pantera стал самым продаваемым?",
            new String[]{"Vulgar Display of Power", "Cowboys from Hell", "Far Beyond Driven", "The Great Southern Trendkill"},
            0, // Vulgar Display of Power
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Кто был вокалистом группы Slayer?",
            new String[]{"Том Арайя", "Дэйв Мастейн", "Джеймс Хэтфилд", "Оззи Осборн"},
            0, // Том Арайя
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Какой альбом Megadeth стал самым продаваемым?",
            new String[]{"Countdown to Extinction", "Peace Sells... but Who's Buying?", "Rust in Peace", "Youthanasia"},
            0, // Countdown to Extinction
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Кто был вокалистом группы Pantera?",
            new String[]{"Фил Ансельмо", "Дэйв Мастейн", "Джеймс Хэтфилд", "Оззи Осборн"},
            0, // Фил Ансельмо
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Какой альбом Sepultura стал самым влиятельным?",
            new String[]{"Roots", "Chaos A.D.", "Arise", "Beneath the Remains"},
            0, // Roots
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Кто был вокалистом группы Sepultura?",
            new String[]{"Макс Кавалера", "Дэйв Мастейн", "Джеймс Хэтфилд", "Оззи Осборн"},
            0, // Макс Кавалера
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Какой альбом Death стал самым влиятельным в дэт-метале?",
            new String[]{"Symbolic", "Human", "Individual Thought Patterns", "The Sound of Perseverance"},
            0, // Symbolic
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Кто был вокалистом группы Death?",
            new String[]{"Чак Шульдинер", "Дэйв Мастейн", "Джеймс Хэтфилд", "Оззи Осборн"},
            0, // Чак Шульдинер
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Какой альбом Cannibal Corpse стал самым влиятельным?",
            new String[]{"Tomb of the Mutilated", "Butchered at Birth", "The Bleeding", "Vile"},
            0, // Tomb of the Mutilated
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Кто был вокалистом группы Cannibal Corpse?",
            new String[]{"Джордж Фишер", "Дэйв Мастейн", "Джеймс Хэтфилд", "Оззи Осборн"},
            0, // Джордж Фишер
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Какой альбом Opeth стал самым влиятельным в прогрессив-метале?",
            new String[]{"Blackwater Park", "Still Life", "Ghost Reveries", "Watershed"},
            0, // Blackwater Park
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Кто был вокалистом группы Opeth?",
            new String[]{"Микаэль Окерфельдт", "Дэйв Мастейн", "Джеймс Хэтфилд", "Оззи Осборн"},
            0, // Микаэль Окерфельдт
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Какой альбом Dream Theater стал самым влиятельным?",
            new String[]{"Images and Words", "Metropolis Pt. 2: Scenes from a Memory", "Awake", "Train of Thought"},
            0, // Images and Words
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Кто был вокалистом группы Dream Theater?",
            new String[]{"Джеймс ЛаБри", "Дэйв Мастейн", "Джеймс Хэтфилд", "Оззи Осборн"},
            0, // Джеймс ЛаБри
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Какой альбом Tool стал самым влиятельным?",
            new String[]{"Lateralus", "Ænima", "10,000 Days", "Fear Inoculum"},
            0, // Lateralus
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Кто был вокалистом группы Tool?",
            new String[]{"Мэйнард Джеймс Кинэн", "Дэйв Мастейн", "Джеймс Хэтфилд", "Оззи Осборн"},
            0, // Мэйнард Джеймс Кинэн
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Какой альбом Meshuggah стал самым влиятельным?",
            new String[]{"Nothing", "Destroy Erase Improve", "Chaosphere", "ObZen"},
            0, // Nothing
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Кто был вокалистом группы Meshuggah?",
            new String[]{"Йенс Кидман", "Дэйв Мастейн", "Джеймс Хэтфилд", "Оззи Осборн"},
            0, // Йенс Кидман
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Какой альбом Gojira стал самым влиятельным?",
            new String[]{"From Mars to Sirius", "The Way of All Flesh", "L'Enfant Sauvage", "Magma"},
            0, // From Mars to Sirius
            10
        ));
        metalQuestions.add(new QuizQuestion(
            "Кто был вокалистом группы Gojira?",
            new String[]{"Джо Дюплантье", "Дэйв Мастейн", "Джеймс Хэтфилд", "Оззи Осборн"},
            0, // Джо Дюплантье
            10
        ));
        genreQuestions.put("Metal", metalQuestions);
    }

    public static List<QuizQuestion> getQuestionsForGenre(String genre) {
        List<QuizQuestion> allQuestions = genreQuestions.get(genre);
        if (allQuestions == null || allQuestions.size() < QUESTIONS_PER_QUIZ) {
            return allQuestions;
        }

        // Перемешиваем вопросы
        List<QuizQuestion> shuffledQuestions = new ArrayList<>(allQuestions);
        Collections.shuffle(shuffledQuestions, random);

        // Возвращаем первые 10 вопросов
        return shuffledQuestions.subList(0, QUESTIONS_PER_QUIZ);
    }
} 