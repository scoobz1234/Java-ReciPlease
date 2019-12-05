using Xamarin.Forms;

namespace ReciPlease
{
    public partial class App : Application
    {
        public static string FilePath;
        public string fileName;

       public App()
        {
            InitializeComponent();

            MainPage = new NavigationPage(new MainPage());
        }

        public App(string filePath)
        {
            InitializeComponent();

            MainPage = new NavigationPage(new MainPage());

            FilePath = filePath;
        }

        protected override void OnStart()
        {
            // Handle when your app starts
        }

        protected override void OnSleep()
        {
            // Handle when your app sleeps
        }

        protected override void OnResume()
        {
            // Handle when your app resumes
        }

        public void OpenFileStart() 
        {
            MainPage = new NavigationPage(new FilePage(fileName));
        }
    }

    public class IncomingFile 
    {
        public string name { get; set; }
    }
}
