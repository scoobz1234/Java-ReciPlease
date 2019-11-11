using ReciPlease.HelperClasses;
using ReciPlease.Classes;
using System.Collections.Generic;
using Xamarin.Forms;
using System;
using SQLite;

namespace ReciPlease
{
    // [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class RecipeCreationPage : ContentPage
    {

        public List<Ingredient> listOfIngredients = new List<Ingredient>();

        public RecipeCreationPage()
        {
            InitializeComponent();
            SetupPickers();
            pkrMeasurementType.SelectedIndex = 0;
            pkrRecipeType.SelectedIndex = 0;
            btnCancel.Clicked += CancelClicked;
            btnCreate.Clicked += CreateClicked;

        }

        void SetupPickers()
        {
            string[] measurements = Enum.GetNames(typeof(Enums.MeasurementTypes));
            string[] recipetypes = Enum.GetNames(typeof(Enums.MeatType));

            pkrMeasurementType.ItemsSource = measurements;
            pkrRecipeType.ItemsSource = recipetypes;
        }

        private async void CancelClicked(object sender, EventArgs e)
        {
            bool isCanceling = await DisplayAlert("Confirmation", "Are you sure you want to Cancel?", "OK", "Cancel");
            if (isCanceling)
            {
                using (SQLiteConnection conn = new SQLiteConnection(App.FilePath)) 
                {
                    conn.DropTable<Recipe>();
                    conn.DropTable<Ingredient>();
                }
                await Navigation.PushAsync(new MainPage());
            }
        }

        private async void CreateClicked(object sender, EventArgs e)
        {
            bool confirmed = await DisplayAlert("Confirmation", 
                "Is this Recipe ready to be created?", "Yes", "No");
            if (confirmed)
            {
                //get meat type from picker...
                Enums.MeatType mt = (Enums.MeatType)Enum.Parse(typeof(Enums.MeatType), 
                    pkrRecipeType.SelectedItem.ToString());
                //create recipe object with name, meat type...
                Recipe r = new Recipe(entRecipeName.Text, mt);


                //need to add the recipe to the recipes database
                using (SQLiteConnection conn = new SQLiteConnection(App.FilePath))
                {
                    //create the table if it doesnt exist...
                    conn.CreateTable<Recipe>();
                    //insert the recipe into the table...
                    conn.Insert(r);

                    //now the ingredients...
                    conn.CreateTable<Ingredient>();

                    foreach (Ingredient i in listOfIngredients)
                    {
                        i.RecipeID = r.Id;
                        conn.Insert(i);
                    }
                }

                await Navigation.PushAsync(new MainPage());
            }
        }

        private void BtnAddIngredient_Clicked(object sender, EventArgs e)
        {
            Enums.MeasurementTypes mt = (Enums.MeasurementTypes)System.Enum.Parse(typeof(Enums.MeasurementTypes), pkrMeasurementType.SelectedItem.ToString());

            Ingredient i = new Ingredient(entIngredientAmount.Text, mt, entIngredientName.Text);
            listOfIngredients.Add(i);

            lvwIngredients.ItemsSource = null;
            lvwIngredients.ItemsSource = listOfIngredients;
        }

        private void BtnRemoveIngredient_Clicked(object sender, EventArgs e)
        {
            Enums.MeasurementTypes mt = (Enums.MeasurementTypes)System.Enum.Parse(typeof(Enums.MeasurementTypes), pkrMeasurementType.SelectedItem.ToString());
            Ingredient i = (Ingredient)lvwIngredients.SelectedItem;
            listOfIngredients.Remove(i);

            lvwIngredients.ItemsSource = null;
            lvwIngredients.ItemsSource = listOfIngredients;
        }
    }
}