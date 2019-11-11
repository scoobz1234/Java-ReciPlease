using System;
using System.Collections.Generic;
using ReciPlease.Classes;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using SQLite;
using ReciPlease.HelperClasses;

namespace ReciPlease
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class IngredientPage : ContentPage
    {
        Recipe selectedRecipe = null;
        Ingredient selectedIngredient = null;

        public IngredientPage(Ingredient ingredient, Recipe recipe)
        {
            selectedRecipe = recipe;
            selectedIngredient = ingredient;
            InitializeComponent();
            SetupPickers();
            ConnectAndRetrieve(ingredient);
        }

        void SetupPickers()
        {
            string[] measurements = Enum.GetNames(typeof(Enums.MeasurementTypes));
            pkrMeasurementType.ItemsSource = measurements;
        }

        private async void CancelClicked(object sender, EventArgs e)
        {
            bool isCanceling = await DisplayAlert("Confirmation", "Are you sure you want to Cancel?", "OK", "Cancel");
            if (isCanceling)
            {
                await Navigation.PushAsync(new RecipePage(selectedRecipe));     
            }
        }

        private async void ConfirmClicked(object sender, EventArgs e) 
        {
            Enums.MeasurementTypes mt = (Enums.MeasurementTypes)System.Enum.Parse(typeof(Enums.MeasurementTypes), pkrMeasurementType.SelectedItem.ToString());
            selectedIngredient.amount = entIngredientAmount.Text;
            selectedIngredient.ingredientName = entIngredientName.Text; 
            selectedIngredient.measurement = mt;

            using (SQLiteConnection conn = new SQLiteConnection(App.FilePath)) 
            {
                conn.Update(selectedIngredient);
            }

            await Navigation.PushAsync(new RecipePage(selectedRecipe));
        }

        private void ConnectAndRetrieve(Ingredient i) 
        {
            
            entIngredientAmount.Text = i.amount ?? "Enter Amount";
            entIngredientName.Text = i.ingredientName ?? "Enter Name";
            pkrMeasurementType.SelectedItem = i.measurement != Enums.MeasurementTypes.None ? i.measurement : Enums.MeasurementTypes.None;

        }

        private async void btnDelete_Clicked(object sender, EventArgs e)
        {
            bool isDeleting = await DisplayAlert("Confirmation", "Are you sure you want to Delete this ingredient?", "Yes", "No");

            if (isDeleting)
            {
                using (SQLiteConnection conn = new SQLiteConnection(App.FilePath))
                {
                    conn.Delete(selectedIngredient);
                    selectedIngredient = null;
                }
                await Navigation.PushAsync(new RecipePage(selectedRecipe));
            }

        }
    }
}