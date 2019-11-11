using System;
using ReciPlease.HelperClasses;
using SQLite;
using SQLiteNetExtensions.Attributes;

namespace ReciPlease.Classes
{
    public class Ingredient
    {
        [PrimaryKey, AutoIncrement]
        public int Id { get; set; }

        public int RecipeID { get; set; }

        public string amount { get; set; }
        public Enums.MeasurementTypes measurement { get; set; }
        public string ingredientName { get; set; }

        public Ingredient() { }
        public Ingredient(string _amount, Enums.MeasurementTypes _measurement, string _ingredientName)
        {
            amount = _amount;
            measurement = _measurement;
            ingredientName = _ingredientName;
        }

        public override string ToString()
        {
            return amount + " " + measurement.ToString() + " " + ingredientName;
        }
    }
}
