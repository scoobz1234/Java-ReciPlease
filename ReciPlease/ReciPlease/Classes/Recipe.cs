using System;
using System.Collections.Generic;
using System.Text;
using ReciPlease.HelperClasses;
using SQLite;

namespace ReciPlease.Classes
{
    public class Recipe
    {
        [PrimaryKey]
        public int Id { get; set; }
        public string recipeName { get; set; }
        public Enums.MeatType type { get; set; }
        public List<Ingredient> ingredients = new List<Ingredient>();

        public Recipe() { }
        //Constructor...
        public Recipe(string _recipeName, Enums.MeatType _type, List<Ingredient> _ingredients)
        {
            recipeName = _recipeName;
            type = _type;
            ingredients = _ingredients;
        }
    }
}
