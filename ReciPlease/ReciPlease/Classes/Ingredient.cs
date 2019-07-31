using ReciPlease.HelperClasses;
using System;
using System.Collections.Generic;
using System.Text;

namespace ReciPlease.Classes
{
    public class Ingredient
    {
        public string amount;
        public Enums.MeasurementTypes measurement;
        public string ingredientName;

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
