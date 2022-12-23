package org.pechblenda.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class AuthHelper {

	fun generateAuthentication(userName: String, authorities: List<GrantedAuthority>): Authentication {
		return object: Authentication {

			var authenticate = true

			override fun getAuthorities(): Collection<GrantedAuthority?> {
				return authorities
			}

			override fun getCredentials(): Collection<GrantedAuthority?> {
				return authorities
			}

			override fun getDetails(): String {
				return ""
			}

			override fun isAuthenticated(): Boolean {
				return authenticate
			}

			override fun setAuthenticated(isAuthenticated: Boolean) {
				authenticate = isAuthenticated
			}

			override fun getName(): String {
				return userName
			}

			override fun getPrincipal(): Any {
				return object: UserDetails {
					override fun getAuthorities(): List<GrantedAuthority> {
						return authorities
					}

					override fun getPassword(): String {
						return ""
					}

					override fun getUsername(): String {
						return name
					}

					override fun isAccountNonExpired(): Boolean {
						return true
					}

					override fun isAccountNonLocked(): Boolean {
						return true
					}

					override fun isCredentialsNonExpired(): Boolean {
						return true
					}

					override fun isEnabled(): Boolean {
						return true
					}
				}
			}
		}
	}

}