package Lesson1.homework.contestants;

public enum Kinds {
    CAT("кот"), HUMAN("человЕК"), ROBOT("РОБОТ");
    private String rusWord;

    Kinds(String rusWord) {
        this.rusWord = rusWord;
    }

    //OKrylov: В чем смысл переопределения данного метода? Мы ведь можем задать в нужном формате эти данные явно, а затем его же и использовать
    public String toString() {
        // преобразование, чтобы слово гарантированно начиналось с заглавной буквы, остальные строчные
        return rusWord.substring(0, 1).toUpperCase() + rusWord.substring(1).toLowerCase();
    }
}
