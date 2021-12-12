package com.binus.cuman

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.binus.cuman.models.GLOBALS
import com.binus.cuman.repositories.UserRepository
import com.binus.cuman.submitData.SubmitDataActivity
import com.binus.cuman.utils.FCMSubscribeUtil
import com.binus.cuman.views.change_password.ChangePasswordActivity
import com.binus.cuman.views.curhat_by_topic.CurhatByTopicFragment
import com.binus.cuman.views.hottest_curhat.HottestCurhatFragment
import com.binus.cuman.views.insert_curhat.InsertCurhatActivity
import com.binus.cuman.views.newest_curhat.NewestCurhatFragment
import com.binus.cuman.views.notification.NotificationActivity
import com.binus.cuman.views.search_curhat.SearchCurhatFragment
import com.binus.cuman.views.settings.SettingsActivity
import com.binus.cuman.views.user_profile.UserProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var appSettingPreferences: SharedPreferences
    var isLarge: Boolean = false

    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigation: BottomNavigationView
    private val PAGES_COUNT = 5
    private val menuIdList: List<Int> = listOf(
        R.id.hottest_menu_item,
        R.id.newest_menu_item,
        R.id.topic_menu_item,
        R.id.search_menu_item,
        R.id.profile_menu_item
    )

    override fun onCreate(savedInstanceState: Bundle?) {

        GLOBALS.CHECK_USER=true

        disableNightMode()
        setFontSize()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTheme(R.style.Theme_Cuman);

        setViewPager()
        setBottomMenuItemListener()
        setNotification()
        checkUser()
    }
    private fun checkUser(){

        if(UserRepository.getCurrentUser()?.uid==null)return
        if(!GLOBALS.CHECK_USER)return
        UserRepository.getUserById(UserRepository.getCurrentUser()?.uid!!)!!.addSnapshotListener{
            user,e ->
            if(user==null||!GLOBALS.CHECK_USER) return@addSnapshotListener;
            else if(user?.get("password")==null){
                val intent  = Intent(this, ChangePasswordActivity::class.java)
                startActivity(intent)
            }
            else if(user?.get("gender") ==null){
                val intent  = Intent(this, SubmitDataActivity::class.java)
                startActivity(intent)
            }


        }
    }

    private fun setFontSize() {
        appSettingPreferences = getSharedPreferences(GLOBALS.SETTINGS_PREFERENCES_NAME, 0)
        isLarge = appSettingPreferences.getBoolean(GLOBALS.SETTINGS_LARGE_KEY, false)


        when (isLarge) {
            true -> {
                val themeID: Int = R.style.Theme_Cuman_FontLarge
                setTheme(themeID)

            }
            else -> {
                val themeID: Int = R.style.Theme_Cuman
                setTheme(themeID)
            }
        }
    }

    fun setNotification() {
        appSettingPreferences = getSharedPreferences(GLOBALS.SETTINGS_PREFERENCES_NAME, 0)

        when (appSettingPreferences.getBoolean(GLOBALS.SETTINGS_NOTIFICATION_KEY, false)) {
            true -> {
                FCMSubscribeUtil.subscribe()
            } else -> {
            FCMSubscribeUtil.unsubscribe()
        }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.action_bar_items, menu)

        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_curhat_menu_item -> {
                moveToAddCurhatActivity()
            }
            R.id.settings_menu_item -> {
                moveToSettingsActivity()
            }
            R.id.notification_menu_item -> {
                moveToNotificationsActivity()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun moveToNotificationsActivity(){
        val intent=Intent(this, NotificationActivity::class.java)
        startActivity(intent)
    }
    private fun moveToSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)

    }

    private fun moveToAddCurhatActivity() {
        val intent = Intent(this, InsertCurhatActivity::class.java)
        startActivity(intent)
    }

    private fun disableNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun setViewPager() {
        val pagerAdapter = CurhatViewSlidePagerAdapter(this)
        viewPager = findViewById(R.id.curhatViewPager)
        viewPager.adapter = pagerAdapter

        val curhatPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigation.selectedItemId = menuIdList[position]
            }
        }

        viewPager.registerOnPageChangeCallback(curhatPageChangeCallback)
    }

    private fun setBottomMenuItemListener() {
        val menuOnItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.hottest_menu_item -> {
                        viewPager.currentItem = 0
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.newest_menu_item -> {
                        viewPager.currentItem = 1
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.topic_menu_item -> {
                        viewPager.currentItem = 2
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.search_menu_item -> {
                        viewPager.currentItem = 3
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.profile_menu_item -> {
                        viewPager.currentItem = 4
                        return@OnNavigationItemSelectedListener true
                    }
                    else -> false
                }
            }

        bottomNavigation = findViewById(R.id.navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(menuOnItemSelectedListener)
    }

    private inner class CurhatViewSlidePagerAdapter(fa: FragmentActivity) :
        FragmentStateAdapter(fa) {

        override fun getItemCount(): Int = PAGES_COUNT

        override fun createFragment(position: Int): Fragment {
            lateinit var currentFragment: Fragment
            when (position) {
                0 -> currentFragment = HottestCurhatFragment()
                1 -> currentFragment = NewestCurhatFragment()
                2 -> currentFragment = CurhatByTopicFragment()
                3 -> currentFragment = SearchCurhatFragment()
                4 -> currentFragment = UserProfileFragment()
            }
            return currentFragment
        }


    }
}