using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;

namespace ReciPlease
{
    [DesignTimeVisible(false)]
    public partial class MainPage : ContentPage
    {
        static RecipeDatabaseController recipeDatabase;

        public MainPage()
        {
            InitializeComponent();
            btnExit.Clicked += ExitClicked;
            btnCreateRecipe.Clicked += CreateRecipeClicked;
        }

        private async void ExitClicked(object sender, EventArgs e)
        {
            bool isExiting = await DisplayAlert("Confirmation","Are you sure you want to exit?", "OK","Cancel");
            if (isExiting)
            {
                System.Diagnostics.Process.GetCurrentProcess().Kill();
            }
        }

        private async void CreateRecipeClicked(object sender, EventArgs e)
        {
            await Navigation.PushAsync(new RecipeCreationPage());
        }

        public static RecipeDatabaseController RecipeDatabase {
            get {
                if (recipeDatabase == null)
                {
                    recipeDatabase = new RecipeDatabaseController();
                }
                return recipeDatabase;
            }
        }

    }
}
