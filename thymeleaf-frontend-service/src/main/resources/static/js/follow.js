window.addEventListener("load", () => {
    let btnSubscribe = document.getElementById("follow");
    let btnUnSubscribe = document.getElementById("unfollow");

    let id_follower = document.getElementById("id_user_profile").value;

    btnSubscribe.addEventListener("click", subscribe);
    btnUnSubscribe.addEventListener("click", unSubscribe);

    let isFollowing = document.getElementById("isFollowing");

    toggleFollow(isFollowing.value);

    function toggleFollow(flag) {
        if (flag == "true") {
            btnSubscribe.style = "display:none";
            btnUnSubscribe.style = "display: block";
        } else {
            btnSubscribe.style = "display:block";
            btnUnSubscribe.style = "display: none";
        }
    }

    function subscribe() {
        $.ajax({
            type:'GET',//тип запроса
            data:{id_follower:id_follower},//параметры запроса
            url : '/profile/subscribe_ajax',//this is url mapping for controller
            success : function(response) {
                let obj = strToObject(response);
                setInfoAboutUser(obj);
                toggleFollow("true");
            },
            error : function() {
                console.log("Error ajax");
            },
            done : function(e) {
                console.log("DONE");
            }
        });
    }

    function unSubscribe() {
        $.ajax({
            type:'POST',//тип запроса
            data:{id_follower:id_follower},//параметры запроса
            url : '/profile/unsubscribe_ajax',//this is url mapping for controller
            success : function(response) {
                let obj = strToObject(response);
                setInfoAboutUser(obj);
                toggleFollow("false");
            },
            error : function() {
                console.log("Error ajax");
            },
            done : function(e) {
                console.log("DONE");
            }
        });
    }


    function setInfoAboutUser(obj) {
        let posts = document.getElementById("post_count");
        let likes = document.getElementById("like_count");
        let follower = document.getElementById("followers_count");
        let following = document.getElementById("following_count");

        posts.innerHTML = obj.postCount;
        follower.innerHTML = obj.followersCount;
        following.innerHTML = obj.followingCount;
        likes.innerHTML = obj.likesCount;
    }
});
