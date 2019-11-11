using System.Collections.Generic;
using ReciPlease.HelperClasses;
using SQLite;
using SQLiteNetExtensions.Attributes;

namespace ReciPlease.Classes
{
    public class Recipe
    {
        [PrimaryKey, AutoIncrement]
        public int Id { get; set; }

        public Recipe() { }
        public Recipe(string _recipeName, Enums.MeatType _type)
        {
            recipeName = _recipeName;
            type = _type;
        }


        public string recipeName { get; set; }
        public Enums.MeatType type { get; set; }

        public override string ToString()
        {
            return this.recipeName + ", "+ this.type.ToString();
        }
    }
}
