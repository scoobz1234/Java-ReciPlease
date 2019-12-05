using System;
using System.ComponentModel;
using Xamarin.Forms;

namespace ReciPlease
{
    [DesignTimeVisible(false)]
    public partial class MainPage : ContentPage
    {

        public MainPage()
        {
            InitializeComponent();
            btnExit.Clicked += ExitClicked;
            btnCreateRecipe.Clicked += CreateRecipeClicked;
            btnShare.Clicked += OptionsClicked;
            btnMenu.Clicked += MenuClicked;
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

        private async void OptionsClicked(object sender, EventArgs e)
        {
            await Navigation.PushAsync(new SharePage());
        }

        private async void MenuClicked(object sender, EventArgs e) 
        {
            await Navigation.PushAsync(new MenuPage());
        }

    }
}
