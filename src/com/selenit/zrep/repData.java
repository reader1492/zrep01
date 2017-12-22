// Valerii Zinovev, Perm, 5 dec 2017
// Структура для выборки данных из ТАСС (POJO-класс)
// Работа с БД first
package com.selenit.zrep;

public class repData {
	public String inn;				// ИНН

	// Конструктор
	public repData() {
		inn = "";
	}
	
    @Override
    public String toString()
    {
        return "ClassPojo repData";
    }
}
